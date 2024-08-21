// Get elements by their IDs
const signUpButton = document.getElementById('signUp');
const signInButton = document.getElementById('signIn');
const container = document.getElementById('container');
const normalUserBtn = document.getElementById('normalUserBtn');
const adminUserBtn = document.getElementById('adminUserBtn');
const adminNameContainer = document.getElementById('adminNameContainer');
const nameSearch = document.getElementById('nameSearch');
const nameList = document.getElementById('nameList');
const loginForm = document.getElementById('loginForm');
const signupForm = document.getElementById('signupForm');
const names = ['스파르타', 'Name2', 'Name3', 'Name4', 'Name5']; // Your list of names

// Handle sign-up and sign-in button clicks
signUpButton.addEventListener('click', () => {
    container.classList.add("right-panel-active");
});

signInButton.addEventListener('click', () => {
    container.classList.remove("right-panel-active");
});

document.addEventListener('DOMContentLoaded', function() {
    const logoLink = document.getElementById('logoLink');

    logoLink.addEventListener('click', function(e) {
        e.preventDefault(); // 기본 앵커 동작 방지
        window.location.href = '/home'; // /home으로 페이지 이동
    });
});

// Handle user role button clicks
normalUserBtn.addEventListener('click', () => {
    normalUserBtn.classList.add('active');
    adminUserBtn.classList.remove('active');
    updateContent('normal');
});

adminUserBtn.addEventListener('click', () => {
    adminUserBtn.classList.add('active');
    normalUserBtn.classList.remove('active');
    updateContent('admin');
});

// Validate address function
function validateAddress() {
    const userAddress = document.getElementById('userAddress');
    const userAddressError = document.getElementById('userAddressError');
    const addressPattern = /^[a-zA-Z0-9가-힣\s,.-]+$/;

    if (!addressPattern.test(userAddress.value)) {
        userAddress.classList.add('invalid');
        userAddressError.style.display = 'block';
        userAddressError.textContent = '영문과 한글, 쉼표(,), 마침표(.), 하이폰(-) 사용 가능합니다.';
        return false;
    } else {
        userAddress.classList.remove('invalid');
        userAddressError.style.display = 'none';
        return true;
    }
}

// Validate username function
function validateUsername() {
    const username = document.getElementById('signupId');
    const usernameError = document.getElementById('signupIdError');
    const usernamePattern = /^[a-z0-9]+$/;

    if (!usernamePattern.test(username.value) || username.value.length < 6 || username.value.length > 20) {
        username.classList.add('invalid');
        usernameError.style.display = 'block';
        usernameError.textContent = '아이디는 소문자 + 숫자로 구성되어야 하며, 6자 이상 20자 이하이어야 합니다.';
        return false;
    } else {
        username.classList.remove('invalid');
        usernameError.style.display = 'none';
        return true;
    }
}

// Validate password function
function validatePassword() {
    const password = document.getElementById('signupPassword');
    const passwordError = document.getElementById('signupPasswordError');
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_])\S{6,}$/;

    if (!passwordPattern.test(password.value) || password.value.length < 6 || password.value.length > 255) {
        password.classList.add('invalid');
        passwordError.style.display = 'block';
        passwordError.textContent = '비밀번호는 알파벳 대소문자 + 숫자 + 특수문자로 구성되어야 하며, 최소 6자 이상이어야 합니다.';
        return false;
    } else {
        password.classList.remove('invalid');
        passwordError.style.display = 'none';
        return true;
    }
}

// Validate name function
function validateName() {
    const name = document.getElementById('signupName');
    const nameError = document.getElementById('signupNameError');
    const namePattern = /^[a-zA-Z가-힣]+$/;

    if (!namePattern.test(name.value) || name.value.length < 2 || name.value.length > 20) {
        name.classList.add('invalid');
        nameError.style.display = 'block';
        nameError.textContent = '이름은 한글 또는 영어로 구성되어야 하며, 2자 이상 20자 이하이어야 합니다.';
        return false;
    } else {
        name.classList.remove('invalid');
        nameError.style.display = 'none';
        return true;
    }
}

// Event listeners for validation on blur
document.getElementById('signupId').addEventListener('blur', validateUsername);
document.getElementById('signupPassword').addEventListener('blur', validatePassword);
document.getElementById('signupName').addEventListener('blur', validateName);
document.getElementById('userAddress').addEventListener('blur', validateAddress);


let userDistinction = '';
// Update content based on user type
function updateContent(userType) {
    const signUpTitle = document.getElementById('signUpTitle');
    const signUpSpan = document.getElementById('signUpSpan');
    const signUpButton = document.getElementById('signUpButton');
    const signInTitle = document.getElementById('signInTitle');
    const signInSpan = document.getElementById('signInSpan');
    const signInButton = document.getElementById('signInButton');
    const forgotPassword = document.getElementById('forgotPassword');
    const welcomeBackTitle = document.getElementById('welcomeBackTitle');
    const welcomeBackText = document.getElementById('welcomeBackText');
    const helloTitle = document.getElementById('helloTitle');
    const helloText = document.getElementById('helloText');
    const userAddress = document.getElementById('userAddress');
    const userAddressError = document.getElementById('userAddressError');

    // Apply fade-out effect to the current content
    document.querySelector('.overlay-left').classList.add('fade-out');
    document.querySelector('.overlay-right').classList.add('fade-out');

    setTimeout(() => {
        if (userType === 'admin') {
            signUpTitle.textContent = '부트캠프 계정 생성';
            signUpSpan.textContent = '또는 아이디로 등록';
            signUpButton.textContent = '부트캠프 계정 등록';
            signInTitle.textContent = '부트캠프 로그인';
            signInSpan.textContent = '또는 계정으로 로그인';
            signInButton.textContent = '부트캠프 로그인';
            forgotPassword.textContent = '부트캠프 비밀번호를 잊으셨나요?';
            welcomeBackTitle.textContent = '환영합니다!';
            welcomeBackText.textContent = '부트캠프 정보를 입력해주세요';
            helloTitle.textContent = '어서오세요!';
            helloText.textContent = '여러분의 부트캠프를 소개해주세요';

            userDistinction = 'BOOTCAMP';

            // Hide the user address input
            userAddress.style.display = 'none';

            // Hide user address error message
            userAddress.classList.remove('invalid');
            userAddressError.style.display = 'none';

            // Show the searchable name input
            adminNameContainer.style.display = 'block';
        } else {
            signUpTitle.textContent = '계정 생성';
            signUpSpan.textContent = '또는 아이디로 등록';
            signUpButton.textContent = '가입하기';
            signInTitle.textContent = '로그인';
            signInSpan.textContent = '또는 계정으로 로그인';
            signInButton.textContent = '로그인';
            forgotPassword.textContent = '비밀번호를 잊으셨나요?';
            welcomeBackTitle.textContent = '환영합니다!';
            welcomeBackText.textContent = '개인 정보로 입력해주세요';
            helloTitle.textContent = '안녕하세요!';
            helloText.textContent = '여러분의 의견을 들려주세요';

            userDistinction = 'USER';

            // Show the user address input
            userAddress.style.display = 'block';

            // Validate inputs if they contain values
            if (userAddress.value.trim() !== '') validateAddress();
            if (document.getElementById('signupId').value.trim() !== '') validateUsername();
            if (document.getElementById('signupPassword').value.trim() !== '') validatePassword();
            if (document.getElementById('signupName').value.trim() !== '') validateName();

            // Hide the searchable name input
            adminNameContainer.style.display = 'none';
        }

        // Apply fade-in effect to the updated content
        document.querySelector('.overlay-left').classList.remove('fade-out');
        document.querySelector('.overlay-right').classList.remove('fade-out');
        document.querySelector('.overlay-left').classList.add('fade-in');
        document.querySelector('.overlay-right').classList.add('fade-in');
    }, 500); // Duration should match the CSS animation duration
}

// Initialize with normal user content
updateContent('normal');



// Populate name list
function populateNameList() {
    nameList.innerHTML = ''; // Clear the list
    names.forEach(name => {
        const li = document.createElement('li');
        li.textContent = name;
        li.addEventListener('click', () => {
            nameSearch.value = name;
            nameList.style.display = 'none';
        });
        nameList.appendChild(li);
    });
}

populateNameList();

// Search functionality
nameSearch.addEventListener('input', () => {
    const filter = nameSearch.value.toLowerCase();
    const items = nameList.querySelectorAll('li');
    items.forEach(item => {
        const text = item.textContent.toLowerCase();
        item.style.display = text.includes(filter) ? '' : 'none';
    });
    nameList.style.display = filter ? 'block' : 'none';
});

// Hide name list if clicking outside
document.addEventListener('click', (event) => {
    if (!adminNameContainer.contains(event.target)) {
        nameList.style.display = 'none';
    }
});

// 유효성 검사 함수
function validateInput(input, pattern, errorMessage) {
    const errorElement = document.getElementById(input.id + 'Error');
    if (!pattern.test(input.value)) {
        input.classList.add('invalid');
        errorElement.textContent = errorMessage;
        errorElement.style.display = 'block';
        return false;
    } else {
        input.classList.remove('invalid');
        errorElement.style.display = 'none';
        return true;
    }
}

// 회원가입 버튼 이벤트 리스너
document.getElementById('signUpButton').addEventListener('click', async (event) => {
    event.preventDefault();

    const username = document.getElementById('signupId');
    const password = document.getElementById('signupPassword');
    const name = document.getElementById('signupName');
    const userAddr = document.getElementById('userAddress');
    const campName = document.getElementById('nameSearch');

    // 유효성 검사 패턴
    const usernamePattern = /^[a-z0-9]{6,20}$/;
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_])\S{6,}$/;
    const namePattern = /^[a-zA-Z가-힣]{2,20}$/;
    const userAddrPattern = /^[a-zA-Z0-9가-힣\s,.-]*$/;

    const isUsernameValid = validateInput(username, usernamePattern, "소문자, 숫자로 구성된 6 ~ 20자 이내로 입력해주세요.");
    const isPasswordValid = validateInput(password, passwordPattern, "대소문자, 숫자, 특수문자로 구성된 최소 6글자 이상으로 입력해 주세요.");
    const isNameValid = validateInput(name, namePattern, "한글 or 영어로 구성된 2 ~ 20자 이내로 입력해주세요.");
    const isUserAddrValid = userAddr.value === "" || validateInput(userAddr, userAddrPattern, "주소는 영문과 한글, 쉼표(,), 마침표(.), 하이폰(-) 사용 가능합니다.");
    const isCampNameValid = !adminUserBtn.classList.contains('active') || campName.value !== "";

    if (!isCampNameValid) {
        campName.classList.add('invalid');
        document.getElementById('nameSearchError').textContent = "부트캠프 이름을 입력해주세요.";
        document.getElementById('nameSearchError').style.display = 'block';
    } else {
        campName.classList.remove('invalid');
        document.getElementById('nameSearchError').style.display = 'none';
    }

    if (isUsernameValid && isPasswordValid && isNameValid && isUserAddrValid && isCampNameValid) {
        const data = {
            username: username.value,
            password: password.value,
            name: name.value,
            userAddr: adminUserBtn.classList.contains('active') ? null : userAddr.value, // 부트캠프 사용자일 경우 null
            campName: adminUserBtn.classList.contains('active') ? campName.value : null, // 부트캠프 사용자일 경우 campName 포함
            userRole: userDistinction
        };

        fetch(`/api/auth/signup`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
        })
        .then(response => response.json())
        .then(data => {
            if (data.statuscode === '201') {
                Swal.fire({
                    title: '회원가입 완료',
                    text: '회원가입이 완료되었습니다.',
                    icon: 'success',
                    confirmButtonText: '확인'
                }).then(() => {
                    window.location.reload();
                });
            } else {
                Swal.fire({
                    title: '회원가입 실패',
                    text: `오류 발생: ${data.msg}`,
                    icon: 'error',
                    confirmButtonText: '확인'
                });
            }
        })
        .catch(error => {
            console.error('서버 오류:', error);
            Swal.fire({
                title: '회원가입 실패',
                text: '서버와의 통신 오류가 발생했습니다.',
                icon: 'error',
                confirmButtonText: '확인'
            });
        });
    }
});

// 로그인 버튼 이벤트 리스너
document.getElementById('signInButton').addEventListener('click', async (event) => {
    event.preventDefault();

    const username = document.getElementById('loginId').value;
    const password = document.getElementById('loginPassword').value;

    const data = {
        username: username,
        password: password,
        role: userDistinction
    };

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        const result = await response.json();

        if (response.ok) {
            localStorage.setItem('accessToken', result.data.accessToken);
            localStorage.setItem('refreshToken', result.data.refreshToken);
            window.location.href = '/home';
        } else {
            Swal.fire({
                title: '로그인 실패',
                text: result.msg || '알 수 없는 오류가 발생했습니다.',
                icon: 'error',
                confirmButtonText: '확인'
            });
        }
    } catch (error) {
        console.error('서버 오류:', error);
        Swal.fire({
            title: '로그인 실패',
            text: '서버와의 통신 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonText: '확인'
        });
    }
});
