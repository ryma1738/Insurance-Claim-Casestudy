export const login = (email, password) => {
    return fetch('/api/v1/user/login', {
        method: 'POST',
        headers: {
            'content-type': 'application/json'
        },
        body: JSON.stringify({
            email: email,
            password: password
        })
    });
}

export const signup = (email, password, phoneNumber, dob) => {
    return fetch('/api/v1/user/create', {
        method: 'POST',
        headers: {
            'content-type': 'application/json'
        },
        body: JSON.stringify({
            email: email,
            password: password,
            phoneNumber: phoneNumber,
            dob: dob
        })
    });
}

export const getRole = (jwt) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/user/role', {
        method: "GET",
        headers: headers
    });
}

export const getUserInfo = (jwt) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/user', {
        method: "GET",
        headers: headers
    });
}

export const  addVehicle = (jwt, make, model, year, vin, useCase) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/user/vehicle', {
        method: "PUT",
        headers: headers,
        body: JSON.stringify({
            make: make,
            model: model,
            year: year,
            vin: vin,
            useCase: useCase
        })
    });
}

export const deleteVehicle = (jwt, id) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/user/vehicle/' + id, {
        method: "DELETE",
        headers: headers
    });
}

export const createClaim = (jwt, userId, vehicleId, description) => {
    const headers = new Headers({
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/claim', {
        method: "POST",
        headers: headers,
        body: JSON.stringify({
            userId: userId,
            vehicleId: vehicleId,
            description: description
        })
    });
}

export const uploadFiles = (jwt, formData, claimId) => {
    const headers = new Headers({
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/file/upload/multiple/' + claimId, {
        method: "POST",
        headers: headers,
        body: formData
    });
}

export const downloadFile = (jwt, docId) => {
    console.log(docId)
    const headers = new Headers({
        'Authorization': "Bearer " + jwt
    });
    return fetch('api/v1/file/download/' + docId, {
        method: "GET",
        headers: headers
    });
}
