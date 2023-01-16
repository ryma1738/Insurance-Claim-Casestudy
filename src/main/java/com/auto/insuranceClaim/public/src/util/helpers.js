import { Spinner } from 'react-bootstrap';


export const createPhoneNumber = (value, pastValue) => {
    if (value.length === 3) {
        if (pastValue.charAt(pastValue.length - 1) !== "-") {
            return value + "-";
        }
    } else if (value.length === 7) {
        if (pastValue.charAt(pastValue.length - 1) !== "-") {
            return value + "-";
        }
    }
    return value;
}

export const convertPhoneNumber = (value) => {
    return value.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3");
}

export const loading = (width, height) => {
    return (
        <div className="d-flex justify-content-center">
            <Spinner animation="border" role="status" style={{ width: width + "px", height: height + "px", marginTop: "10vh" }}>
                <span className="visually-hidden">Loading...</span>
            </Spinner>
        </div>
    )
}

export const formatDate = date => {
    return `${new Date(date).getMonth() + 1}/${new Date(date).getDate()}/${new Date(
        date
    ).getFullYear()}`;
}