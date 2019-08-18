const DEFAULT_CONTENT_TYPE = 'application/json;charset=UTF-8';
const header = {
    'Content-Type': DEFAULT_CONTENT_TYPE
}

const api = {
    GET: url => fetch(url, { method: 'GET' }),
    POST: (url, data) => fetch(url, {
        method: 'POST',
        headers: header,
        body: JSON.stringify(data)
    }),
    PUT: (url, data) => fetch(url, {
        method: 'PUT',
        headers: header,
        body: JSON.stringify(data)
    }),
    DELETE: (url) => fetch(url, {method: 'DELETE'})
}