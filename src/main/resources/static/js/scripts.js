// 테마 토글 함수
function toggleTheme() {
    const body = document.body;
    const toggleCircle = document.querySelector('.toggle-circle');
    body.classList.toggle('dark');
    toggleCircle.classList.toggle('dark');

    // 현재 테마를 localStorage에 저장
    if (body.classList.contains('dark')) {
        localStorage.setItem('theme', 'dark');
    } else {
        localStorage.setItem('theme', 'light');
    }
}

// 페이지 로드 시 저장된 테마를 적용
window.addEventListener('DOMContentLoaded', (event) => {
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
        const body = document.body;
        const toggleCircle = document.querySelector('.toggle-circle');
        if (savedTheme === 'dark') {
            body.classList.add('dark');
            toggleCircle.classList.add('dark');
        } else {
            body.classList.remove('dark');
            toggleCircle.classList.remove('dark');
        }
    }
});

// 사이드바 토글 함수
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const content = document.querySelector('.content');
    const openButton = document.querySelector('.open-sidebar-button');

    sidebar.classList.toggle('closed');
    openButton.classList.toggle('hidden');

    if (sidebar.classList.contains('closed')) {
        content.style.marginLeft = '30px';
        content.style.marginRight = '0px';
    } else {
        content.style.marginLeft = '270px';
        content.style.marginRight = '-250px';
    }
}

// 페이지 로드 시 로그인 상태를 체크하여 버튼을 업데이트하는 함수
function checkLoginStatus() {
    const loginButton = document.querySelector('#loginButton');
    const adminButton = document.querySelector('#adminButton');
    const accessToken = localStorage.getItem('accessToken');

    if (accessToken) {
        const payloadBase64 = accessToken.split('.')[1];
        const decodedPayload = atob(payloadBase64);
        const payload = JSON.parse(decodedPayload);

        console.log('디코딩된 사용자 정보:', payload);

        const userRole = payload.auth;

        console.log('User role:', userRole);

        if (userRole && userRole.trim().toUpperCase() === 'ADMIN') {
            adminButton.style.display = 'inline-block';
        }

        loginButton.textContent = 'Logout';
        loginButton.onclick = onLogout;
    } else {
        loginButton.textContent = 'Login';
        loginButton.onclick = () => location.href = '/auth';
        adminButton.style.display = 'none';
    }
}

// 로그아웃 성공 시 호출되는 함수
function onLogoutSuccess() {
    const loginButton = document.querySelector('#loginButton');
    const adminButton = document.querySelector('#adminButton');
    loginButton.textContent = 'Login';
    loginButton.onclick = () => location.href = '/auth';
    adminButton.style.display = 'none'; // 로그아웃 후 ADMIN 버튼 숨기기

    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');

    Swal.fire({
        toast: true,
        position: 'center',
        icon: 'success',
        title: '로그아웃이 정상적으로 실행되었습니다.',
        showConfirmButton: false,
        timer: 3000,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.addEventListener('mouseenter', Swal.stopTimer);
            toast.addEventListener('mouseleave', Swal.resumeTimer);
        },
        customClass: {
            title: 'black-text'
        }
    });
}

function openAlarmPopup() {
    const width = 630;
    const height = 600;

    const screenWidth = window.screen.width;
    const screenHeight = window.screen.height;

    const left = (screenWidth - width) / 2;
    const top = (screenHeight - height) / 2;

    window.open('/alarm', 'Alarm Popup', `width=${width},height=${height},top=${top},left=${left}`);
}

document.addEventListener('DOMContentLoaded', function() {
    const logoLink = document.getElementById('logoLink');

    logoLink.addEventListener('click', function(e) {
        e.preventDefault(); // 기본 앵커 동작 방지
        window.location.href = '/home'; // /home으로 페이지 이동
    });
});

// 로그아웃 요청을 보내는 함수
async function onLogout() {
    try {
        // 실제 로그아웃 API 호출
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            onLogoutSuccess();
        } else {
            alert('로그아웃 실패');
        }

    } catch (error) {
        alert('로그아웃 중 오류 발생: ' + error.message);
    }
}

async function updateUnreadNotificationCount() {
    const token = localStorage.getItem('accessToken');
    if (!token) {
        console.error('로그인 토큰을 찾을 수 없습니다.');
        return;
    }

    const payloadBase64 = token.split('.')[1];
    const decodedPayload = atob(payloadBase64);
    const payload = JSON.parse(decodedPayload);
    const username = payload.sub;

    try {
        const response = await fetch(`/api/notifications/unread-count/${username}`, {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const unreadCount = await response.json();
            console.log('Unread count:', unreadCount);
            const alarmButton = document.getElementById('alarmButton');
            const unreadCountBadge = document.getElementById('unreadCountBadge');

            if (unreadCount > 0) {
                unreadCountBadge.textContent = unreadCount;
                unreadCountBadge.style.display = 'inline-block';
            } else {
                unreadCountBadge.style.display = 'none';
            }
        } else {
            console.error('알림 개수를 가져오는 데 실패했습니다.');
        }
    } catch (error) {
        console.error('알림 개수를 가져오는 중 오류가 발생했습니다:', error);
    }
}

// DOMContentLoaded 이벤트 리스너에 로그인 상태 체크 함수 추가
document.addEventListener('DOMContentLoaded', (event) => {
    toggleSidebar();
    checkLoginStatus(); // 로그인 상태를 체크하는 함수 호출
    updateUnreadNotificationCount();
});

function refreshPage() {
    location.reload();
}
