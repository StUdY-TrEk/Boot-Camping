let currentRequestType = 'user';

document.addEventListener('DOMContentLoaded', () => {
    displayRequests('all', 'newest');
});

// API로부터 프로필 요청 데이터를 가져오는 함수
async function loadRequests() {
    let apiUrl = `/api/admin/profiles/role/${currentRequestType.toUpperCase()}`;

    try {
        const response = await fetch(apiUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        if (response.ok) {
            const result = await response.json();
            // 상태가 PENDING, APPROVED, REJECTED인 경우만 반환
            return result.data.filter(req => ['PENDING', 'APPROVED', 'REJECTED'].includes(req.status));
        } else {
            console.error('Failed to fetch profiles');
            return [];
        }
    } catch (error) {
        console.error('Error fetching profiles:', error);
        return [];
    }
}

// 요청 데이터를 화면에 표시하는 함수
async function displayRequests(category = 'all', sortOrder = 'newest') {
    const requestsList = document.getElementById('requestsList');
    requestsList.innerHTML = '';

    const requests = await loadRequests();
    let filteredRequests = requests;

    // 카테고리에 따라 필터링
    if (category !== 'all') {
        filteredRequests = requests.filter(req => req.status === category.toUpperCase());
    }

    // 정렬 순서에 따라 정렬
    if (sortOrder === 'newest') {
        filteredRequests.sort((a, b) => new Date(b.modifiedAt) - new Date(a.modifiedAt));
    } else {
        filteredRequests.sort((a, b) => new Date(a.modifiedAt) - new Date(b.modifiedAt));
    }

    // 각 요청 데이터를 카드 형태로 화면에 추가
    filteredRequests.forEach(request => {
        const requestElement = createProfileCard(request);
        requestsList.appendChild(requestElement);
    });

    addEventListeners();
}

// 프로필 요청을 카드 형태로 생성하는 함수
function createProfileCard(profile) {
    const profileElement = document.createElement('div');
    profileElement.className = 'request';

    profileElement.innerHTML = `
        <h3>${profile.bootcampName || profile.name}</h3>
        <div class="request-details">
            <p>${currentRequestType === 'user' ? `트랙: ${profile.track}` : `관계자 이름: ${profile.name}`}</p>
            ${currentRequestType === 'user' ? `<p>기수: ${profile.generation}</p>` : ''}
            <p>요청일: ${new Date(profile.requestedAt).toLocaleDateString()}</p>
            <p>상태: <span class="${profile.status === 'REJECTED' ? 'warning-text' : profile.status === 'APPROVED' ? 'emphasis-text' : ''}">${getStatusText(profile.status)}</span></p>
        </div>
        <div class="request-actions">
            <button class="btn btn-details" data-id="${profile.id}">상세 정보</button>
            ${profile.status === 'PENDING' ? `
                <button class="btn btn-approve" data-id="${profile.id}">승인</button>
                <button class="btn btn-reject" data-id="${profile.id}">거절</button>
            ` : ''}
        </div>
    `;
    return profileElement;
}

// 상태 텍스트를 변환하는 함수
function getStatusText(status) {
    switch (status) {
        case 'PENDING': return '대기중';
        case 'APPROVED': return '승인됨';
        case 'REJECTED': return '거절됨';
        default: return '알 수 없음';
    }
}

// 각 버튼에 이벤트 리스너를 추가하는 함수
function addEventListeners() {
    document.querySelectorAll('.btn-details').forEach(button => {
        button.addEventListener('click', showDetails);
    });

    document.querySelectorAll('.btn-approve').forEach(button => {
        button.addEventListener('click', approveRequest);
    });

    document.querySelectorAll('.btn-reject').forEach(button => {
        button.addEventListener('click', rejectRequest);
    });
}

// 상세 정보를 표시하는 함수
async function showDetails(e) {
    const requestId = e.target.getAttribute('data-id');
    const apiUrl = `/api/admin/profiles/${requestId}`;

    try {
        const response = await fetch(apiUrl, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        if (response.ok) {
            const result = await response.json();
            const request = result.data;

            const modalContent = document.getElementById('modalContent');

            if (currentRequestType === 'user') {
                modalContent.innerHTML = `
                    <p><strong>이름:</strong> ${request.name}</p>
                    <p><strong>부트캠프:</strong> ${request.bootcampName}</p>
                    <p><strong>트랙:</strong> ${request.track}</p>
                    <p><strong>기수:</strong> ${request.generation}</p>
                    <p><strong>시작일:</strong> ${request.startDate}</p>
                    <p><strong>종료일:</strong> ${request.endDate}</p>
                    <p><strong>요청일:</strong> ${new Date(request.requestedAt).toLocaleDateString()}</p>
                    <p><strong>상태:</strong> <span class="${request.status === 'REJECTED' ? 'warning-text' : request.status === 'APPROVED' ? 'emphasis-text' : ''}">${getStatusText(request.status)}</span></p>
                    <p><strong>인증서:</strong></p>
                    <img src="${request.certificate}" alt="인증서" class="certificate-preview">
                `;
            } else if (currentRequestType === 'bootcamp') {
                modalContent.innerHTML = `
                    <p><strong>관계자 이름:</strong> ${request.name}</p>
                    <p><strong>부트캠프명:</strong> ${request.bootcampName}</p>
                    <p><strong>요청일:</strong> ${new Date(request.requestedAt).toLocaleDateString()}</p>
                    <p><strong>상태:</strong> ${getStatusText(request.status)}</p>
                    <p><strong>사업자 등록증:</strong></p>
                    <img src="${request.certificate}" alt="사업자 등록증" class="certificate-preview">
                `;
            }

            openModal('detailModal');
        } else {
            console.error('Failed to fetch profile details');
        }
    } catch (error) {
        console.error('Error fetching profile details:', error);
    }
}

// 요청을 승인하는 함수
async function approveRequest(e) {
    const requestId = parseInt(e.target.getAttribute('data-id'));
    const apiUrl = `/api/admin/profiles/${requestId}/approve`;

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        if (response.ok) {
            console.log(`승인된 요청 ID: ${requestId}`);
            displayRequests('all', 'newest');
        } else {
            console.error('Failed to approve request');
        }
    } catch (error) {
        console.error('Error approving request:', error);
    }
}

// 요청을 거절하는 함수
async function rejectRequest(e) {
    const requestId = parseInt(e.target.getAttribute('data-id'));
    const apiUrl = `/api/admin/profiles/${requestId}/reject`;

    try {
        const response = await fetch(apiUrl, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
            }
        });

        if (response.ok) {
            console.log(`거절된 요청 ID: ${requestId}`);
            displayRequests('all', 'newest');
        } else {
            console.error('Failed to reject request');
        }
    } catch (error) {
        console.error('Error rejecting request:', error);
    }
}

// 모달을 여는 함수
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.style.display = "flex";
}

// 모달을 닫는 함수
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.style.display = "none";
}

// 모달 닫기 버튼 이벤트 리스너
document.querySelectorAll('.close').forEach(closeBtn => {
    closeBtn.addEventListener('click', function() {
        this.closest('.modal').style.display = "none";
    });
});

// 필터 변경 이벤트 리스너
document.getElementById('filterDropdown').addEventListener('change', function(e) {
    const activeTab = document.querySelector('.category-tab.active');
    displayRequests(activeTab.getAttribute('data-category'), e.target.value);
});

// 카테고리 탭 이벤트 리스너
document.querySelectorAll('.category-tab').forEach(tab => {
    tab.addEventListener('click', function() {
        document.querySelectorAll('.category-tab').forEach(t => t.classList.remove('active'));
        this.classList.add('active');
        displayRequests(this.getAttribute('data-category'), document.getElementById('filterDropdown').value);
    });
});

// 승인 요청 유형 탭 이벤트 리스너
document.querySelectorAll('.approval-type-tab').forEach(tab => {
    tab.addEventListener('click', function() {
        document.querySelectorAll('.approval-type-tab').forEach(t => t.classList.remove('active'));
        this.classList.add('active');
        currentRequestType = this.getAttribute('data-type');
        document.getElementById('requestTypeTitle').textContent =
            currentRequestType === 'user' ? '승인 요청(유저)' : '승인 요청(부트캠프)';
        displayRequests('all', 'newest');
    });
});

// 모달 배경 클릭 이벤트 추가
document.querySelectorAll('.modal').forEach(modal => {
    modal.addEventListener('click', (event) => {
        if (event.target === modal) {
            closeModal(modal.id);
        }
    });
});

document.addEventListener('DOMContentLoaded', () => {
    // 부트캠프 등록 버튼 클릭 시 모달 열기
    document.getElementById('btnBootcampRegister').addEventListener('click', function() {
        openModal('registerModal');
    });

    // 모달 내의 등록하기 버튼 클릭 시 부트캠프 등록 처리
    document.getElementById('btnRegister').addEventListener('click', async () => {
        const bootcampName = document.getElementById('bootcampName').value;
        const description = document.getElementById('description').value;
        const imageFile = document.getElementById('imageFile').files[0];
        // 입력값 검증
        if (!bootcampName || !description || !imageFile) {
            Swal.fire({
                icon: 'warning',
                title: '필수 입력 사항',
                text: '부트캠프 이름, 설명, 그리고 이미지를 모두 입력해주세요.'
            });
            return;
        }

        // 요청 데이터 생성
        const formData = new FormData();
        formData.append('name', bootcampName);
        formData.append('description', description);
        formData.append('imageFile', imageFile);

        try {
            const response = await fetch('/api/admin/camps', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
                },
                body: formData
            });

            if (response.ok) {
                Swal.fire({
                    icon: 'success',
                    title: '부트캠프 등록 완료',
                    text: '부트캠프가 성공적으로 등록되었습니다.'
                });
                closeModal('registerModal');
            } else {
                const errorData = await response.json();
                Swal.fire({
                    icon: 'error',
                    title: '등록 실패',
                    text: errorData.message || '부트캠프 등록에 실패했습니다.'
                });
            }
        } catch (error) {
            Swal.fire({
                icon: 'error',
                title: '오류 발생',
                text: '부트캠프 등록 중 오류가 발생했습니다.'
            });
            console.error('Error registering bootcamp:', error);
        }
    });
});