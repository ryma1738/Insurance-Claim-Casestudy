import React, { useState, useEffect, } from 'react';
import { Col, Container, Row, Modal} from 'react-bootstrap';
import {saveAs} from 'file-saver';
import { getUserInfo, addVehicle, deleteVehicle, createClaim, uploadFiles, downloadFile} from '../util/api';
import {formatDate} from '../util/helpers';
import {makes} from '../util/make';

function Main() {
    const [userInfo, setUserInfo] = useState(null);
    const [userInfoHTML, setUserInfoHTML] = useState(<></>);
    const [vehicleInfoHTML, setVehicleInfoHTML] = useState(<></>);
    const [claimInfoHTML, setClaimInfoHTML] = useState(<></>);
    const [showVehicleModel, setShowVehicleModel] = useState(false);
    const [showClaimModel, setShowClaimModel] = useState(false);
    const [showUploadModel, setShowUploadModel] = useState(false);
    const [claimVehicleId, setClaimVehicleId] = useState(null);
    const [description, setDescription] = useState(null);
    const [activeClaimId, setActiveClaimId] = useState(null);

    const [make, setMake] = useState(null);
    const [model, setModel] = useState(null);
    const [year, setYear] = useState(null);
    const [vin, setVin] = useState(null);
    const [useCase, setUseCase] = useState(null);

    async function loadInfo() {
        const response = await getUserInfo(localStorage.getItem('jwtInsuranceCase'));
        if (response.ok) {
            const data = await response.json();
            setUserInfo(data);
            setUserInfoHTML(
                <Col className="loginDiv"xs={6} key={data.id}>
                    <h4 className="mt-2">Your info:</h4>
                    <p>Email: {data.email}</p>
                    <p>Phone Number: {data.phoneNumber}</p>
                    <p>Date of Birth: {formatDate(data.dob)}</p>
                </Col>
            );
            if (data.vehicles.length === 0) {
                setVehicleInfoHTML(
                    
                    <Col className="loginDiv mx-1" xs={5}>
                        <div>
                            <h4 className="mt-2 text-center fs-50">Please enter your Vehicle info:</h4>
                        </div>
                        <div className="d-flex justify-content-center mb-3">
                            <button type='button' className='button'
                             onClick={(e) => setShowVehicleModel(true)}>Add Vehicle</button>
                        </div>
                                   
                    </Col>
                );
            } else {
                setVehicleInfoHTML(
                    <>
                        {data.vehicles.map(vehicle =>
                        <Col className="loginDiv mx-1 pt-2" xs={5} key={vehicle.id}>
                            <p>Vehicle Make: {vehicle.make}</p>
                            <p>Vehicle Model: {vehicle.model}</p>
                            <p>Vehicle Year: {vehicle.year}</p>
                            <p>Vin: {vehicle.vin}</p>
                            <p>Vehicle Use Type: {vehicle.useCase}</p>
                            <div className='d-flex mb-2'>
                                <button type="button" className="deleteButton ms-auto" 
                                    onClick={(e) => attemptDeleteVehicle(e, vehicle.id, vehicle.make,vehicle.model)}>
                                Delete Vehicle</button>
                            </div>
                        </Col>  
                        )}
                        <Col className="mx-1" xs={5}>
                            <div className="d-flex justify-content-center my-3">
                                <button type='button' className='button'
                                    onClick={(e) => setShowVehicleModel(true)}>Add Another Vehicle</button>
                            </div>
                        </Col>
                    </>
                );
            }
            if (data.claims.length === 0) {
                setClaimInfoHTML(
                    <Col className="loginDiv mx-1" xs={12}>
                        <div>
                            <h4 className="mt-2 text-center fs-50">You have no claims</h4>
                        </div>
                        <div className="d-flex justify-content-center mb-3">
                            <button type='button' className='button'
                                onClick={(e) => setShowClaimModel(true)}>Create Claim</button>
                        </div>
                    </Col>
                );
            } else {
                setClaimInfoHTML(
                <>
                    {data.claims.map(claim =>
                        <Col className="loginDiv mx-1 pt-2" xs={5} key={claim.id}>
                            <p>Vehicle Involved: {claim.vehicle.make + " "+ claim.vehicle.model + " " + claim.vehicle.year}</p>
                            <p>{claim.description}</p>
                            <p>Claim Status: {claim.claimStatus}</p>
                            <p>Claim Created:{formatDate(claim.createdAt)}</p>
                            {claim.documents.length === 0 ? 
                            <p className='text-center'>You have not uploaded any documents</p> 
                            : claim.documents.map(doc =>
                                <div className='d-flex'>
                                    <p className='fs-7 my-auto' style={{width: "85%"}}>{doc.fileName}</p>
                                    <button type="button" className="button ms-auto fs-7 py-1 my-1"
                                        onClick={(e) => attemptDownloadDoc(e, doc.id, doc.fileName)}>Download</button>
                                </div>
                            )}
                            <div className='d-flex mb-2'>
                                <button type ="button" className="button mx-auto"
                                    onClick={() => onUploadOpen(claim.id)}>Upload Documents</button>
                            </div>
                        </Col>
                    )}
                    <Col className="mx-1" xs={5}>
                        <div className="d-flex justify-content-center my-3">
                            <button type='button' className='button'
                                onClick={(e) => setShowClaimModel(true)}>Create New Claim</button>
                        </div>
                    </Col>
                </>);
            }
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

    async function attemptAddVehicle(e) {
        e.preventDefault();
        if (window.confirm("Are you sure you want to add this vehicle?") === true) {
            const response = await addVehicle(localStorage.getItem("jwtInsuranceCase"),
                make, model, year, vin, useCase);
            if (response.ok) {
                alert("Vehicle was added successfully!");
                onVehicleModalClosed(null);
                loadInfo();
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
        } else return;
    } 

    async function attemptDeleteVehicle(e, id, make, model) {
        e.preventDefault();
        if (window.confirm("Are you sure you want to delete your " + make + " " + model + "?") === true) {
            const response = await deleteVehicle(localStorage.getItem("jwtInsuranceCase"), id);
            if (response.ok) {
                alert("Vehicle was deleted successfully!");
                loadInfo();
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
        } else return;
    }

    async function attemptCreateClaim(e) {
        e.preventDefault();
        if (window.confirm("Are you sure you want to create this claim?") === true) {
            const response = await createClaim(localStorage.getItem("jwtInsuranceCase"), userInfo.id, claimVehicleId, description);
            if (response.ok) {
                alert("Claim was created successfully!");
                onClaimModalClosed(null);
                loadInfo();
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
        } else return;
    }

    async function attemptUpload(e) {
        e.preventDefault();
        const myForm = document.getElementById("documentForm");
        let formData = new FormData(myForm);
        const response = await uploadFiles(localStorage.getItem("jwtInsuranceCase"), formData, activeClaimId);
        if (response.ok) {
            alert("File Uploaded Successfully!");
            loadInfo();
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

    async function attemptDownloadDoc(e, docId, fileName) {
        e.preventDefault();
        const response = await downloadFile(localStorage.getItem("jwtInsuranceCase"), docId);
        console.log(response);
        if (response.ok){
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

    function onVehicleModalClosed(e) {
        if (e !== null) e.preventDefault();
        setMake(null);
        setModel(null);
        setYear(null);
        setVin(null);
        setUseCase(null);
        setShowVehicleModel(false);
    }

    function onClaimModalClosed(e) {
        if (e !== null) e.preventDefault();
        setClaimVehicleId(null);
        setDescription(null);
        setShowClaimModel(false);
    }

    function onUploadOpen(claimId) {
        setActiveClaimId(claimId);
        setShowUploadModel(true);
    }

    function onUploadClose() {
        setActiveClaimId(null);
        setShowUploadModel(false);
    }

    useEffect(() => {
        if (localStorage.getItem('jwtInsuranceCase') !== null) {
            loadInfo();
        } else window.location = "/login";
    }, [])

    return (
    <Container>
        <Row>
            <h2 className="lobster fs-50 mt-3 text-center">Your Account</h2>
        </Row>
        <Row className="d-flex justify-content-center align-items-center">
            {userInfoHTML}
        </Row>
        <Row className="d-flex justify-content-center align-items-center">
            <h3 className="lobster fs-50 mt-3 text-center">Your Vehicles</h3>
            {vehicleInfoHTML}
        </Row>
            <Row className="d-flex justify-content-center align-items-center mb-5">
                <h3 className="lobster fs-50 mt-3 text-center">Your Claims</h3>
                {claimInfoHTML}
            </Row>
            <Modal show={showVehicleModel} onClose={(e) => setShowVehicleModel(false)}>
                <Modal.Header>
                    <Modal.Title>Add Vehicle</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Container fluid>
                        <Row>
                            <form id="vehicleForm" onSubmit={(e) => {attemptAddVehicle(e)}}>
                                <div className="my-3 d-flex">
                                    <label htmlFor="make">Make: </label>
                                    <select className="ms-auto py-1" value={make} id="make" required style={{ width: "85%" }}
                                    onChange={(e) => setMake(e.target.value)}>
                                        <option value={null}></option>
                                        {makes.map(make => <option value={make} key={make}>{make}</option>)}
                                    </select>
                                </div>
                                <div className="my-3 d-flex">
                                    <label htmlFor="model">Model: </label>
                                    <input type="text" id="model" value={model} className="ms-auto" required style={{ width: "85%" }}
                                    onChange={(e) => setModel(e.target.value)} />
                                </div>
                                <div className="my-3 d-flex">
                                    <label htmlFor="year">Year: </label>
                                    <input type="number" max={2025} min={1910} value={year} id="year" className="ms-auto" required 
                                    style={{ width: "85%" }} onChange={(e) => setYear(e.target.value)} />
                                </div>
                                <div className="my-3 d-flex">
                                    <label htmlFor="vin">Vin #: </label>
                                    <input type="text" id="vin" value={vin} minLength={17} maxLength={17} className="ms-auto" required 
                                    style={{width: "85%"}} onChange={(e) => setVin(e.target.value)} />
                                </div>
                                <div className="my-3 d-flex">
                                    <label htmlFor="make">Use Case: </label>
                                    <select className="ms-auto py-1" id="make" value={useCase} required style={{ width: "80%" }}
                                    onChange={(e) => setUseCase(e.target.value)}>
                                        <option value={null}></option>
                                        <option value="Commute">Commute</option>
                                        <option value="Pleasure">Pleasure</option>
                                        <option value="Business">Business</option>
                                    </select>
                                </div>
                            </form>
                        </Row>
                    </Container>
                </Modal.Body>
                <Modal.Footer>
                    <button variant="secondary" className="button" onClick={(e) => onVehicleModalClosed(e)}>Close</button>
                    <button type="submit" form='vehicleForm' className="button">Add Vehicle</button>
                </Modal.Footer>
            </Modal>
            <Modal show={showClaimModel} onClose={(e) => onUploadClose()}>
                <Modal.Header>
                    <Modal.Title>Create Claim</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Container fluid>
                        <Row>
                            <form id="claimForm" onSubmit={(e) => { attemptCreateClaim(e) }}>
                                <div className="my-3 d-flex">
                                    <label htmlFor="claimVehicleId">Vehicle: </label>
                                    <select className="ms-auto py-1" value={claimVehicleId} id="claimVehicleId" required style={{ width: "85%" }}
                                        onChange={(e) => setClaimVehicleId(e.target.value)}>
                                        <option value={null}>Vehicle Involved</option>
                                        {userInfo ? userInfo.vehicles.map(vehicle => 
                                            <option value={vehicle.id} key={vehicle.id}>{vehicle.make + " " + vehicle.model
                                        }</option>) : <></>}
                                    </select>
                                </div>
                                <div className="my-3 d-flex">
                                    <textarea id="description" value={description} className="ms-auto mx-1" required style={{ width: "100%" }}
                                        onChange={(e) => setDescription(e.target.value)} rows={4} maxLength={1500}  minLength={100}
                                        placeholder="Please give a description of the accident and damages done to the vehicle."/>
                                </div>
                                
                            </form>
                        </Row>
                    </Container>
                </Modal.Body>
                <Modal.Footer>
                    <button variant="secondary" className="button" onClick={(e) => onClaimModalClosed(e)}>Close</button>
                    <button type="submit" form='claimForm' className="button">Create Claim</button>
                </Modal.Footer>
            </Modal>
            <Modal show={showUploadModel} onClose={(e) => setShowUploadModel(false)}>
                <Modal.Header>
                    <Modal.Title>Upload Documents</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Container fluid>
                        <Row>
                            <form id="documentForm" onSubmit={(e) => { attemptUpload(e) }}>
                                <div className="my-3 d-flex">
                                    <input type="file" className="ms-auto" name="files" style={{width: "100%"}} multiple/>
                                </div>
                            </form>
                        </Row>
                    </Container>
                </Modal.Body>
                <Modal.Footer>
                    <button variant="secondary" className="button" onClick={(e) => onUploadClose()}>Close</button>
                    <button type="submit" form='documentForm' className="button">Upload</button>
                </Modal.Footer>
            </Modal>
    </Container>
    );
}

export default Main;