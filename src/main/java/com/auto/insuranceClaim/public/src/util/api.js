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
    })
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
            dob: Date.of(dob)
        })
    })
}

export const getRole = (jwt) => {
    const headers = new Headers({
        'Content-Type': 'application/json',
        'Authorization': "Bearer " + jwt
    });
    return fetch('/api/v1/user/role', {
        method: "GET",
        headers: headers
    })
}