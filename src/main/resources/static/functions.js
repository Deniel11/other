async function createGetRequest(url) {
    try {
        const response = await fetch(url);

        const data = await response.json();

        if (!response.ok) {
            throw data;
        }
        return data;
    } catch (error) {
        return error;
    }
}

async function createRequest(requestType,url,sendData) {
    try {
        const request = new Request(url, {
            method: requestType,
            headers: {
                Accept: 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(sendData)
        });

        const response = await fetch(request);

        const data = await response.json();

        if (!response.ok) {
            if(data == null){
                throw new Error(response.statusText);
            }
            throw data;
        }
        return data;
    } catch (error) {
        return error;
    }
}