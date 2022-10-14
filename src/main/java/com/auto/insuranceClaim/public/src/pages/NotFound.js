import { Col, Container, Row } from "react-bootstrap";

const NotFound = () => {

    return (
        <Container fluid>
            <Row className="mt-5">
                <svg xmlns="http://www.w3.org/2000/svg" width="160" height="160" fill="currentColor" viewBox="0 0 16 16">
                    <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z" />
                    <path d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z" />
                </svg>
            </Row>
            <Row>
                <p className="text-center fs-1 mt-4">404 Page not Found</p>
            </Row>
            <Row >
                <div className="d-flex justify-content-center">
                    <button type="button" className="button" onClick={() => window.location.replace("/")}>Return to home page</button>
                </div>
            </Row>
        </Container>
    );
}

export default NotFound;