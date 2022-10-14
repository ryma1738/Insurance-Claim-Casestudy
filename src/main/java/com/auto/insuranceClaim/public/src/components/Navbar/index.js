import React, {useState, useEffect} from 'react';
import {Container, Row, Col} from 'react-bootstrap';
import {getRole} from '../../util/api';
function Navigator() {

    const [jwt, setJwt] = useState(null);
    const [navbar, setNavbar] = useState(<></>);
    const [role, setRole] = useState(null);
    

    async function getJWT() {
        setJwt(localStorage.getItem("jwtInsuranceCase"))
        if (localStorage.getItem("jwtInsuranceCase") === null) 
            setNavbar((<p className="my-0"><a href="/login" className="text-decoration-none text-link">Login or Signup</a></p>));
        else {
            const response = await getRole(localStorage.getItem("jwtInsuranceCase"));
            if (response.ok) {
                const data = await response.json();
                localStorage.setItem('roleInsuranceCase', data.role);
                setRole(data.role);
                setNavbar((
                    <div className="d-flex">
                        {role === "employee" || data.role === "employee" ? <>
                            <p className="my-0"><a href="/claims" className="text-link text-decoration-none">Claims</a></p>
                            <p className="my-0 mx-3 fw-bolder">|</p>
                        </> : <></>}
                        <p className="my-0 text-link" onClick={(e) => logout(e)}>Logout</p>
                    </div>));
            } 
            else if (response.status === 400) {
                const error = await response.json();
                console.log(error);
                alert.error(error.message);
            } else if (response.status === 401) {
                localStorage.removeItem("jwtInsuranceCase");
                localStorage.removeItem("roleInsuranceCase");
                getJWT();
                window.location = "/login";
            } else if (response.status === 403 ) {
                alert("You do not have permission to access this!");
                window.location = "/";
            } else {
                alert("An unknown error has occurred, please try again later!")
            }
            
        }
    }

    function logout(e) {
        e.preventDefault();
        localStorage.removeItem("jwtInsuranceCase");
        localStorage.removeItem("roleInsuranceCase");
        getJWT();
        window.location = "/";
    }

    useEffect(() => {
        getJWT();
    } ,[])

    return (
        <Container fluid className="nav-bar">
            <Row className="mx-2 d-flex justify-content-center align-content-center">
                
                <Col xs={7} className="">
                    <a href="/" className="no-link">
                    <h1 className=" fs-100 lobster m-0">Insurance Claims</h1>
                    </a>
                </Col>
                
                <Col xs={4} className="d-flex justify-content-end align-content-center my-auto navbarText">
                    {navbar}
                </Col>
            </Row>
        </Container>
    )
}

export default Navigator;