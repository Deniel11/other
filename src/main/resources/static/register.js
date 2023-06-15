'use strict';
document.addEventListener('DOMContentLoaded', async () => {
    const nameInput = document.getElementById("name-input");
    const registerButton = document.getElementById("register-button");
    let outputMessage = document.getElementById("output-message");

    registerButton.addEventListener("click", async (event) => {
        event.preventDefault();
        let sendData = {
            name: nameInput.value
        };
        let registerUser = await createRequest('POST', "/register", sendData);
        if(registerUser.error){
            outputMessage.innerText = registerUser.error;
            outputMessage.style.visibility = "visible";
        } else {
            outputMessage.style.visibility = "hidden";
            try {
                window.location.href = "/";
            } catch (error){
                outputMessage.innerText = error;
                outputMessage.style.visibility = "visible";
            }
        }
    });
});