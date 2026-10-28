const loginForm =
    document.getElementById("loginForm");

const message =
    document.getElementById("message");

const loginScreen =
    document.getElementById("loginScreen");

const dashboardScreen =
    document.getElementById("dashboardScreen");

const welcomeMessage =
    document.getElementById("welcomeMessage");

const displayAccountNumber =
    document.getElementById("displayAccountNumber");

const logoutButton =
    document.getElementById("logoutButton");

const dashboardMessage =
    document.getElementById("dashboardMessage");


let currentAccount = null;


// LOGIN
loginForm.addEventListener(
    "submit",
    async function (event) {

        event.preventDefault();


        const accountNumber =
            document.getElementById(
                "accountNumber"
            ).value;


        const pin =
            document.getElementById(
                "pin"
            ).value;

        // Validate account number
        if (
            accountNumber === "" ||
            !/^\d+$/.test(accountNumber)
        ) {

            message.textContent =
                "Please enter a valid account number.";

            return;
        }

        // Validate PIN
        if (!/^\d{4}$/.test(pin)) {

            message.textContent =
                "PIN must contain exactly 4 digits.";

            return;
        }

        message.textContent =
            "Logging in...";


        try {

            const response =
                await fetch(
                    "http://localhost:8080/api/login",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({

                            accountNumber:
                                Number(accountNumber),

                            pin: pin

                        })
                    }
                );


            const data =
                await response.json();


            if (data.success) {

                currentAccount = {

                    accountNumber:
                        Number(accountNumber),

                    name:
                        data.name

                };


                loginScreen.classList
                    .add("hidden");


                dashboardScreen.classList
                    .remove("hidden");


                welcomeMessage.textContent =
                    "Welcome, "
                    + data.name;


                displayAccountNumber.textContent =
                    accountNumber;


                message.textContent = "";


            } else {

                message.textContent =
                    data.message;

            }


        } catch (error) {

            console.error(error);

            message.textContent =
                "Unable to connect to ATM server.";

        }

    }
);


// LOGOUT
logoutButton.addEventListener(
    "click",
    function () {

        currentAccount = null;


        dashboardScreen.classList
            .add("hidden");


        loginScreen.classList
            .remove("hidden");


        document.getElementById(
            "accountNumber"
        ).value = "";


        document.getElementById(
            "pin"
        ).value = "";


        dashboardMessage.textContent = "";

    }
);

// CHECK BALANCE

const balanceButton =
    document.getElementById("balanceButton");

balanceButton.addEventListener(
    "click",
    async function () {

        if (currentAccount === null) {

            dashboardMessage.textContent =
                "Please login first.";

            return;
        }

        dashboardMessage.textContent =
            "Checking balance...";

        try {

            const response =
                await fetch(
                    "http://localhost:8080/api/balance?accountNumber="
                    + currentAccount.accountNumber
                );

            const data =
                await response.json();

            if (data.success) {

                dashboardMessage.textContent =
                    "Current Balance: ₹"
                    + data.balance;

            } else {

                dashboardMessage.textContent =
                    data.message;
            }

        } catch (error) {

            console.error(error);

            dashboardMessage.textContent =
                "Unable to connect to ATM server.";
        }
    }
);

// WITHDRAW MONEY

const withdrawButton =
    document.getElementById("withdrawButton");

withdrawButton.addEventListener(
    "click",
    async function () {

        if (currentAccount === null) {

            dashboardMessage.textContent =
                "Please login first.";

            return;
        }

        const amount =
            prompt("Enter amount to withdraw:");

        if (amount === null) {
            return;
        }

        const withdrawAmount =
            Number(amount);

        if (
            isNaN(withdrawAmount) ||
            withdrawAmount <= 0 ||
            !Number.isInteger(withdrawAmount)
        ) {

            dashboardMessage.textContent =
                "Please enter a valid amount.";

            return;
        }

        dashboardMessage.textContent =
            "Processing withdrawal...";

        try {

            const response =
                await fetch(
                    "http://localhost:8080/api/withdraw",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            accountNumber:
                                currentAccount.accountNumber,

                            amount:
                                withdrawAmount
                        })
                    }
                );

            const data =
                await response.json();

            if (data.success) {

                dashboardMessage.textContent =
                    "Withdrawal successful! " +
                    "Withdrawn: ₹"
                    + data.amount
                    + " | Remaining Balance: ₹"
                    + data.balance;

            } else {

                dashboardMessage.textContent =
                    data.message;
            }

        } catch (error) {

            console.error(error);

            dashboardMessage.textContent =
                "Unable to connect to ATM server.";
        }
    }
);

// DEPOSIT MONEY

const depositButton =
    document.getElementById("depositButton");

depositButton.addEventListener(
    "click",
    async function () {

        if (currentAccount === null) {

            dashboardMessage.textContent =
                "Please login first.";

            return;
        }

        const amount =
            prompt("Enter amount to deposit:");

        if (amount === null) {
            return;
        }

        const depositAmount =
            Number(amount);

        if (
            isNaN(depositAmount) ||
            depositAmount <= 0 ||
            !Number.isInteger(depositAmount)
        ) {

            dashboardMessage.textContent =
                "Please enter a valid amount.";

            return;
        }

        dashboardMessage.textContent =
            "Processing deposit...";

        try {

            const response =
                await fetch(
                    "http://localhost:8080/api/deposit",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({
                            accountNumber:
                                currentAccount.accountNumber,

                            amount:
                                depositAmount
                        })
                    }
                );

            const data =
                await response.json();

            if (data.success) {

                dashboardMessage.textContent =
                    "Deposit successful! " +
                    "Deposited: ₹"
                    + data.amount
                    + " | Current Balance: ₹"
                    + data.balance;

            } else {

                dashboardMessage.textContent =
                    data.message;
            }

        } catch (error) {

            console.error(error);

            dashboardMessage.textContent =
                "Unable to connect to ATM server.";
        }
    }
);

// TRANSFER MONEY

const transferButton =
    document.getElementById("transferButton");

transferButton.addEventListener(
    "click",
    async function () {

        if (currentAccount === null) {

            dashboardMessage.textContent =
                "Please login first.";

            return;
        }

        const receiver =
            prompt("Enter receiver account number:");

        if (receiver === null) {
            return;
        }

        const receiverAccountNumber =
            Number(receiver);

        if (
            isNaN(receiverAccountNumber) ||
            receiverAccountNumber <= 0 ||
            !Number.isInteger(transferAmount)
        ) {

            dashboardMessage.textContent =
                "Please enter a valid receiver account number.";

            return;
        }

        const amount =
            prompt("Enter amount to transfer:");

        if (amount === null) {
            return;
        }

        const transferAmount =
            Number(amount);

        if (
            isNaN(transferAmount) ||
            transferAmount <= 0
        ) {

            dashboardMessage.textContent =
                "Please enter a valid amount.";

            return;
        }

        if (
            receiverAccountNumber ===
            currentAccount.accountNumber
        ) {

            dashboardMessage.textContent =
                "You cannot transfer money to your own account.";

            return;
        }

        dashboardMessage.textContent =
            "Processing transfer...";

        try {

            const response =
                await fetch(
                    "http://localhost:8080/api/transfer",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({

                            accountNumber:
                                currentAccount.accountNumber,

                            receiverAccountNumber:
                                receiverAccountNumber,

                            amount:
                                transferAmount
                        })
                    }
                );

            const data =
                await response.json();

            if (data.success) {

                dashboardMessage.textContent =
                    "Transfer successful! " +
                    "Transferred: ₹"
                    + data.amount
                    + " | Remaining Balance: ₹"
                    + data.balance;

            } else {

                dashboardMessage.textContent =
                    data.message;
            }

        } catch (error) {

            console.error(error);

            dashboardMessage.textContent =
                "Unable to connect to ATM server.";
        }
    }
);

// MINI STATEMENT

const statementButton =
    document.getElementById("statementButton");

statementButton.addEventListener(
    "click",
    async function () {

        if (currentAccount === null) {

            dashboardMessage.textContent =
                "Please login first.";

            return;
        }

        dashboardMessage.textContent =
            "Loading mini statement...";

        try {

            const response =
                await fetch(
                    "http://localhost:8080/api/statement?accountNumber="
                    + currentAccount.accountNumber
                );

            const data =
                await response.json();

            if (data.success) {

                if (data.transactions.length === 0) {

                    dashboardMessage.innerHTML =
                        "<h3>MINI STATEMENT</h3>" +
                        "<p>No transactions yet.</p>" +
                        "<p>Current Balance: ₹"
                        + data.balance
                        + "</p>";

                    return;
                }

                let statementHTML =
                    "<h3>MINI STATEMENT</h3>";

                statementHTML +=
                    "<p>Current Balance: ₹"
                    + data.balance
                    + "</p>";

                statementHTML +=
                    "<hr>";

                data.transactions.forEach(
                    function (transaction) {

                        statementHTML +=
                            "<p>" +
                            "<strong>"
                            + transaction.type
                            + "</strong>"
                            + " | ₹"
                            + transaction.amount
                            + " | Balance: ₹"
                            + transaction.balanceAfter
                            + " | ID: "
                            + transaction.transactionId
                            + "</p>";
                    }
                );

                dashboardMessage.innerHTML =
                    statementHTML;

            } else {

                dashboardMessage.textContent =
                    data.message;
            }

        } catch (error) {

            console.error(error);

            dashboardMessage.textContent =
                "Unable to connect to ATM server.";
        }
    }
);

// CHANGE PIN

const changePinButton =
    document.getElementById("changePinButton");

changePinButton.addEventListener(
    "click",
    async function () {

        if (currentAccount === null) {

            dashboardMessage.textContent =
                "Please login first.";

            return;
        }

        const oldPin =
            prompt("Enter your current PIN:");

        if (oldPin === null) {
            return;
        }

        const newPin =
            prompt("Enter your new 4-digit PIN:");

        if (newPin === null) {
            return;
        }

        if (!/^\d{4}$/.test(newPin)) {

            dashboardMessage.textContent =
                "New PIN must contain exactly 4 digits.";

            return;
        }

        const confirmPin =
            prompt("Confirm your new PIN:");

        if (confirmPin === null) {
            return;
        }

        if (newPin !== confirmPin) {

            dashboardMessage.textContent =
                "New PIN and confirmation PIN do not match.";

            return;
        }

        dashboardMessage.textContent =
            "Changing PIN...";

        try {

            const response =
                await fetch(
                    "http://localhost:8080/api/change-pin",
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body: JSON.stringify({

                            accountNumber:
                                currentAccount.accountNumber,

                            oldPin:
                                oldPin,

                            newPin:
                                newPin
                        })
                    }
                );

            const data =
                await response.json();

            if (data.success) {

                dashboardMessage.textContent =
                    "PIN changed successfully.";

            } else {

                dashboardMessage.textContent =
                    data.message;
            }

        } catch (error) {

            console.error(error);

            dashboardMessage.textContent =
                "Unable to connect to ATM server.";
        }
    }
);