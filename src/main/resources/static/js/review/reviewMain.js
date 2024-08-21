const reviewsPerPage = 9;  // 한 페이지당 개수
let currentPage = 1;
let totalReviews = 0;
let totalPages = 0;
const accessToken = localStorage.getItem('accessToken');

// API에서 리뷰 데이터를 가져오는 함수
function fetchReviews() {
    return fetch(`/api/reviews?page=${currentPage - 1}&size=${reviewsPerPage}`)
    .then(response => response.json())
    .then(data => {
        if (data.statuscode === "200") {
            totalReviews = data.data.totalElements;
            totalPages = data.data.totalPages;
            return data.data.content;
        } else {
            console.error('에러:', data.msg);
            return [];
        }
    })
    .catch(error => {
        console.error('API 호출 중 에러 발생:', error);
        return [];
    });
}

// 날짜를 "2024. 7. 31." 형식으로 변환하는 함수
function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
    const day = date.getDate();
    return `${year}. ${month}. ${day}.`;
}

// 리뷰 카드 생성 함수
function createReviewCard(review) {
    return `
        <div class="review-card" onclick="goToReviewDetail(${review.id})">
            <div class="review-header">
                <div class="reviews review-title">${review.title}</div>
                <div class="reviews heart-icon heart-button"><i class="far fa-heart"></i></div>
            </div>
            <div class="reviewer-info">
                <span class="reviews review-category">${review.category}</span>
                <span class="reviews review-category">${review.trek}</span>
                <span class="reviews review-date">${formatDate(review.createdAt)}</span>
            </div>
            <div class="review-stars">${'★'.repeat(review.scope)}${'☆'.repeat(5 - review.scope)}</div>
            <p class="reviews review-content">${review.content}</p>
            <span class="reviews review-camp-name">- ${review.campName || '캠프 이름 미상'}</span>
        </div>
    `;
}


function goToReviewDetail(reviewId) {
    const url = new URL(`/review/${reviewId}`, window.location.origin);
    window.location.href = url.href;
}

// 페이지 렌더링 함수
function renderPage() {
    fetchReviews().then(reviews => {
        const container = document.getElementById('reviewContainer');
        container.innerHTML = '';
        reviews.forEach(review => {
            container.innerHTML += createReviewCard(review);
        });
        updatePaginationButtons();
        renderPageNumbers();
        addHeartButtonListeners();
    });
}

// 페이지 변경 함수
function changePage(direction) {
    currentPage += direction;
    renderPage();
}

// 페이지네이션 버튼 업데이트 함수
function updatePaginationButtons() {
    document.getElementById('prevButton').disabled = (currentPage === 1);
    document.getElementById('nextButton').disabled = (currentPage === totalPages);
}

// 페이지 번호 렌더링 함수
function renderPageNumbers() {
    const pageNumbersContainer = document.getElementById('pageNumbers');
    pageNumbersContainer.innerHTML = '';

    if (totalPages <= 1) {
        const pageNumber = document.createElement('span');
        pageNumber.classList.add('page-number', 'active');
        pageNumber.textContent = '1';
        pageNumbersContainer.appendChild(pageNumber);
        return;
    }

    const maxVisiblePages = 5;
    let startPage = Math.max(1, currentPage - Math.floor(maxVisiblePages / 2));
    let endPage = Math.min(totalPages, startPage + maxVisiblePages - 1);

    if (endPage - startPage + 1 < maxVisiblePages) {
        startPage = Math.max(1, endPage - maxVisiblePages + 1);
    }

    if (startPage > 1) {
        pageNumbersContainer.appendChild(createPageButton(1));
        if (startPage > 2) {
            const ellipsis = document.createElement('span');
            ellipsis.classList.add('ellipsis');
            ellipsis.textContent = '...';
            pageNumbersContainer.appendChild(ellipsis);
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        pageNumbersContainer.appendChild(createPageButton(i));
    }

    if (endPage < totalPages) {
        if (endPage < totalPages - 1) {
            const ellipsis = document.createElement('span');
            ellipsis.classList.add('ellipsis');
            ellipsis.textContent = '...';
            pageNumbersContainer.appendChild(ellipsis);
        }
        pageNumbersContainer.appendChild(createPageButton(totalPages));
    }
}

// 페이지 버튼 생성 함수
function createPageButton(pageNumber) {
    const pageButton = document.createElement('span');
    pageButton.classList.add('page-number');
    if (pageNumber === currentPage) {
        pageButton.classList.add('active');
    }
    pageButton.textContent = pageNumber;
    pageButton.onclick = () => {
        currentPage = pageNumber;
        renderPage();
    };
    return pageButton;
}

function addHeartButtonListeners() {
    document.querySelectorAll('.heart-button').forEach(button => {
        button.addEventListener('click', function(event) {
            event.stopPropagation();
            const icon = this.querySelector('i');
            if (icon.classList.contains('far')) {
                icon.classList.remove('far');
                icon.classList.add('fas');
            } else {
                icon.classList.remove('fas');
                icon.classList.add('far');
            }
        });
    });
}


function addReview() {
    if (!accessToken) {
        alert('로그인이 필요합니다.');
        window.location.href = '/auth';
    } else {
        const addUrl = document.querySelector('.write-review-button').getAttribute('review-add-url');
        window.location.href = addUrl;
    }
}

// 초기 페이지 렌더링
window.addEventListener('load', function() {
    var allElements = document.getElementsByTagName('*');
    Array.prototype.forEach.call(allElements, function(el) {
        var includePath = el.dataset.includePath;
        if (includePath) {
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
                if (this.readyState == 4 && this.status == 200) {
                    el.outerHTML = this.responseText;
                }
            };
            xhttp.open('GET', includePath, true);
            xhttp.send();
        }
    });
    renderPage();  // 초기 페이지 렌더링 추가
});





// 현재 페이지에 맞는 결과만 업데이트합니다.
function updateResultsForCurrentPage() {
    const resultsContainer = document.getElementById('resultsContainer');
    resultsContainer.innerHTML = ''; // 기존 결과를 지웁니다.

    const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
    const endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allResults.length);

    for (let i = startIndex; i < endIndex; i++) {
        const rank = allResults[i];
        const camp = rank.camp;
        const track = camp.track;
        const environment = camp.environment;
        const cost = camp.cost;
        const name = camp.name;

        const resultItem = document.createElement('div');
        resultItem.classList.add('ranking-item');
        resultItem.setAttribute('data-track', track);
        resultItem.setAttribute('data-environment', environment);
        resultItem.setAttribute('data-cost', cost);

        resultItem.innerHTML = `
      <button class="like-button">❤</button>
      <h3>${name}</h3>
      <div class="rating">Ranking: ${rank.ranking}</div>
      <div class="review-preview">
        "${camp.description}"
      </div>
      <div class="review-meta">
        작성자: John D. | 날짜: 2023-05-15
      </div>
    `;

        resultsContainer.appendChild(resultItem);
    }

    applyLikeButtonListeners(); // 새로 추가된 항목에 좋아요 버튼 리스너 적용
    applyHoverAnimationListeners(); // 새로 추가된 항목에 호버 애니메이션 리스너 적용
    updatePagination(); // 페이지네이션 업데이트
}

// 결과를 업데이트하여 화면에 표시합니다.
function updateResults(data) {
    allResults = data; // 전체 결과를 저장합니다.
    totalPages = Math.ceil(allResults.length / ITEMS_PER_PAGE); // 총 페이지 수 계산
    currentPage = 1; // 페이지 초기화
    updateResultsForCurrentPage(); // 현재 페이지의 결과만 업데이트
}

// DOMContentLoaded 이벤트가 발생하면 필터링된 결과를 업데이트합니다.
document.addEventListener('DOMContentLoaded', () => {
    const filteredResults = JSON.parse(localStorage.getItem('filteredResults'));

    if (filteredResults) {
        updateResults(filteredResults); // 필터링된 결과를 화면에 업데이트합니다.
        localStorage.removeItem('filteredResults'); // 필터링된 결과를 로컬 스토리지에서 제거합니다.
    } else {
        applyFilters(); // 필터링된 결과가 없을 경우 초기 필터 적용
    }
});


document.addEventListener('DOMContentLoaded', addHeartButtonListeners);