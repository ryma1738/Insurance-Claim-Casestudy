import React, { useState, useEffect, } from 'react';
import {Container, Row, Col, Modal} from 'react-bootstrap';
import {getClaimsByStatus, getClaims, updateClaimStatus, downloadFileEmployee} from '../util/api';
import {formatDate} from '../util/helpers';
import { saveAs } from 'file-saver';

function Claims() {
    const [claimsHTML, setClaimsHTML] = useState(<></>);
    const [currentClaimStatus, setCurrentClaimStatus] = useState("PROCESSING");
    const [individualClaimHTML, setIndividualClaimHTML] = useState(<></>);
    const [showClaimModal, setShowClaimModal] = useState(false);
    const [changeClaimStatus, setChangeClaimStatus] = useState("PROCESSING");

    async function initialLoad() {
        if (localStorage.getItem('roleInsuranceCase') === "employee" || localStorage.getItem('roleInsuranceCase') === "admin") {
            attemptGetClaims(null);
        } else {
            alert("You do not have permission to access this page! Redirecting to the home page...");
            window.location = '/login';
        }
    }

    async function attemptGetClaims(e) {
        if (e !== null) e.preventDefault();
        if (currentClaimStatus === "ALL") {
            const response = await getClaims(localStorage.getItem('jwtInsuranceCase'));
            if(response.ok) {
                const data = await response.json();
                if (data.length === 0) {
                    setClaimsHTML(<Col className="fs-100 lobster text-center">No Claims were found!</Col>)
                } else createClaimsHTML(data);
            } else if (response.status === 400) {
                const error = await response.json();
                console.log(error);
                alert.error(error.message);
            } else if (response.status === 401 || response.status === 403) {
                localStorage.removeItem("jwtInsuranceCase");
                localStorage.removeItem("roleInsuranceCase");
                window.location = "/login";
            } else {
                alert("An unknown error has occurred, please try again later!")
            }
        } else {
            const response = await getClaimsByStatus(localStorage.getItem('jwtInsuranceCase'), currentClaimStatus);
            if (response.ok) {
                const data = await response.json();
                if(data.length === 0) {
                    setClaimsHTML(<Col className="fs-100 lobster text-center">No Claims were found!</Col>)
                } else createClaimsHTML(data);
            } else if (response.status === 400) {
                const error = await response.json();
                console.log(error);
                alert.error(error.message);
            } else if (response.status === 401 || response.status === 403) {
                localStorage.removeItem("jwtInsuranceCase");
                localStorage.removeItem("roleInsuranceCase");
                window.location = "/login";
            } else {
                alert("An unknown error has occurred, please try again later!")
            }
        }
    }

    function createClaimsHTML(data) {
        setClaimsHTML(data.map(claim =>
            <Col xs={5} className="mx-2 my-2 loginDiv" key={claim.id}>
                <p className='mt-2'>Users ID: {claim.user.id}</p>
                <p>Vehicle Involved: {claim.vehicle.make + " " + claim.vehicle.model + " " + claim.vehicle.year}</p>
                <p>{claim.description}</p>
                <p>Claim Status: {claim.claimStatus}</p>
                <p>Claim Created:{formatDate(claim.createdAt)}</p>
                <div className='d-flex mb-2'>
                    <button type="button" className="button mx-auto"
                        onClick={() => onViewIndividualClaim(claim)}>View Claim</button>
                </div>
            </Col>
        ));
    }

    async function attemptDownloadDoc(e, docId, fileName) {
        e.preventDefault();
        const response = await downloadFileEmployee(localStorage.getItem("jwtInsuranceCase"), docId);
        console.log(response);
        if (response.ok) {
            const blob = await response.blob();
            saveAs(blob, fileName);
        } else if (response.status === 400) {
            const error = await response.json();
            console.log(error);
            alert.error(error.message);
        } else if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("jwtInsuranceCase");
            localStorage.removeItem("roleInsuranceCase");
            window.location = "/login";
        } else {
            alert("An unknown error has occurred, please try again later!")
        }
    }

    async function attemptUpdateStatus(e, claimId) {
        e.preventDefault();
        if (e.target[0].value !== "") {
            const response = await updateClaimStatus(localStorage.getItem('jwtInsuranceCase'), claimId, e.target[0].value);
            if (response.ok) {
                alert("Claim Status has been updated!");
                initialLoad();
                setShowClaimModal(false);
            } else if (response.status === 400) {
                const error = await response.json();
                console.log(error);
                alert.error(error.message);
            } else if (response.status === 401 || response.status === 403) {
                localStorage.removeItem("jwtInsuranceCase");
                localStorage.removeItem("roleInsuranceCase");
                window.location = "/login";
            } else {
                alert("An unknown error has occurred, please try again later!")
            }
        }
    }
    

    function onViewIndividualClaim(claim) {
        setChangeClaimStatus(null);
        setIndividualClaimHTML(
            <div>
                <p>Claim ID: {claim.id}</p>
                <p>Users ID: {claim.user.id}</p>
                <p>Users Email: {claim.user.email}</p>
                <p>Users Phone Number: {claim.user.phoneNumber}</p>
                <p>Vehicle Involved: {claim.vehicle.make + " " + claim.vehicle.model + " " + claim.vehicle.year}</p>
                <p>Vehicle VIN: {claim.vehicle.vin}</p>
                <p>{claim.description}</p>
                <p>Claim Status: {claim.claimStatus}</p>
                <p>Claim Created:{formatDate(claim.createdAt)}</p>
                <div className='loginDiv p-2 mb-2'>
                    <h4>Documents</h4>
                    {claim.documents.length === 0 ?
                        <p className='text-center'>No Documents have been uploaded yet</p>
                        : claim.documents.map(doc =>
                            <div className='d-flex'>
                                <p className='fs-6point5 my-auto' style={{ width: "85%" }}>{doc.fileName}</p>
                                <button type="button" className="button ms-auto fs-7 py-1 my-1"
                                    onClick={(e) => attemptDownloadDoc(e, doc.id, doc.fileName)}>Download</button>
                            </div>
                        )}
                </div>
                
                <form className='ms-auto d-flex align-items-center loginDiv p-2' id="updateClaimStatus" 
                    onSubmit={(e) => attemptUpdateStatus(e, claim.id, changeClaimStatus)}>
                    <label htmlFor='updateStatus'>Update Status:</label>
                    <select className='me-2 ms-auto' 
                    onChange={(e) => setChangeClaimStatus(e.target.value)}>
                        <option value={null} disabled selected></option>
                        {claim.claimStatus === "PROCESSING" ? <option value="PROCESSING" disabled>Processing</option> :
                         <option value="PROCESSING">Processing</option>}
                        {claim.claimStatus === "APPROVED" ? <option value="APPROVED" disabled>Approved</option> :
                         <option value="APPROVED">Approved</option>}
                        {claim.claimStatus === "REJECTED" ? <option value="REJECTED" disabled>Rejected</option> :
                         <option value="REJECTED">Rejected</option>}
                    </select>
                </form>
            </div>
        );
        setShowClaimModal(true);
    }

    function onClaimModalClosed(e) {
        e.preventDefault();
        setShowClaimModal(false);
        setIndividualClaimHTML(<></>);
    }

    useEffect(() => {
        if (localStorage.getItem('jwtInsuranceCase') !== null) {
            initialLoad();
        } else window.location = "/login";
    }, []);

    return (
        <Container>
            <Row className="claimsSearchDiv">
                <div className='d-flex align-items-center my-2'>
                    <h3 className='lobster m-0'>Search Claims</h3>
                    <form className='ms-auto d-flex align-items-center' onSubmit={(e) => attemptGetClaims(e)}>
                        {/* <p className='m-0 pe-2' >Claim ID: </p>
                        <input type="number" className="" style={{width: "20%"}} onChange={(e) => attemptSearchById(e.target.value)} />
                        <p className='m-0 px-2'>or</p> */}
                        <select className='me-2' value={currentClaimStatus} onChange={(e) => setCurrentClaimStatus(e.target.value)}>
                            <option value="ALL">All</option>
                            <option value="PROCESSING">Processing</option>
                            <option value="APPROVED">Approved</option>
                            <option value="REJECTED">Rejected</option>
                        </select>
                        <button type='submit' className='button py-0'>Search</button>
                    </form>
                </div>
            </Row>
            <Row>
                {claimsHTML}
            </Row>
            <Modal show={showClaimModal} onClose={(e) => setShowClaimModal(false)}>
                <Modal.Header>
                    <Modal.Title>Claim Information</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Container fluid>
                        <Row>
                            {individualClaimHTML}
                        </Row>
                    </Container>
                </Modal.Body>
                <Modal.Footer>
                    <button variant="secondary" className="button" onClick={(e) => onClaimModalClosed(e)}>Close</button>
                    <button type="submit" form='updateClaimStatus' className="button">Update Status</button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
}

export default Claims;