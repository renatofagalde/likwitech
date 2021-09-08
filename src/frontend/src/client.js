import fetch from 'unfetch';

const checkStatus = response => {

    if (response.ok) {
        return response;
    }

    //convert non 2xx HTTP response into error
    const error = new Error(response.statusText);
    error.response = response;
    console.error('🔥🔥🔥🔥 error 🔥🔥🔥\n'+response.statusText)
    return Promise.reject(error);
}

export const getAllStudents = () =>
    fetch("/api/v001/students")
        .then(checkStatus);

export const addNewStudent = student =>
    fetch("/api/v001/students", {
        headers: {
            'Content-Type': 'application/json'
        },
        method: 'POST',
        body: JSON.stringify(student)
    }).then(checkStatus);

export const deleteStudent = studentId =>
    fetch(`api/v001/students/${studentId}`, {
        method: 'DELETE'
    }).then(checkStatus);

