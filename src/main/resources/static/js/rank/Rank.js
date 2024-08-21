// 페이지당 항목 수와 현재 페이지 변수
const ITEMS_PER_PAGE = 9;
let currentPage = 1;
let totalPages = 1;
let allResults = [];

// API 엔드포인트
const apiUrl = '/api/ranks';

// 페이지네이션 기능을 업데이트합니다.
function updatePagination() {
  const paginationContainer = document.getElementById('pageNumbers');
  const prevButton = document.getElementById('prevButton');
  const nextButton = document.getElementById('nextButton');

  paginationContainer.innerHTML = ''; // 기존 페이지 번호를 지웁니다.

  // 페이지 번호 추가
  for (let i = 1; i <= totalPages; i++) {
    const pageNumber = document.createElement('button');
    pageNumber.textContent = i;
    pageNumber.classList.add('page-number');
    if (i === currentPage) {
      pageNumber.classList.add('active');
    }
    pageNumber.addEventListener('click', () => {
      currentPage = i;
      fetchAndDisplayRanks();
    });
    paginationContainer.appendChild(pageNumber);
  }

  // Previous 버튼 상태 업데이트
  prevButton.disabled = currentPage === 1;

  // Next 버튼 상태 업데이트
  nextButton.disabled = currentPage === totalPages;
}

// 현재 페이지에 맞는 결과만 업데이트합니다.
function displayRanks(data) {
  const resultsContainer = document.getElementById('resultsContainer');
  resultsContainer.innerHTML = ''; // 기존 결과를 지웁니다.

  if (!Array.isArray(data) || data.length === 0) {
    resultsContainer.innerHTML = '<p>No results found.</p>';
    return;
  }

  data.forEach(rank => {
    const campId = rank.campId;
    const campName = rank.campName;
    const description = rank.description;
    const campImage = rank.campImage;
    const ranking = rank.ranking;
    const likesCount = rank.likesCount || 0;

    let emoji = '';
    if (ranking === 1) {
      emoji = '🥇';
    } else if (ranking === 2) {
      emoji = '🥈';
    } else if (ranking === 3) {
      emoji = '🥉';
    }

    const resultItem = document.createElement('div');
    resultItem.classList.add('ranking-item');

    resultItem.innerHTML = `
        <button class="like-button" data-camp-id="${campId}" data-likes-count="${likesCount}">❤ ${likesCount}</button>
        <div class="emoji">${emoji}</div>
        <h3>${campName}</h3>
        <img src="${campImage}" alt="${campName} Image" class="camp-image">
        <p class="camp-description">${description}</p>
        <div class="rating">${ranking}등</div>
    `;

    resultsContainer.appendChild(resultItem);
  });

  applyLikeButtonListeners(); // 새로 추가된 항목에 좋아요 버튼 리스너 적용
  applyHoverAnimationListeners(); // 새로 추가된 항목에 호버 애니메이션 리스너 적용
  updatePagination(); // 페이지네이션 업데이트
}

// API를 호출하여 순위 데이터를 가져옵니다.
function fetchAndDisplayRanks() {
  fetch(`${apiUrl}?page=${currentPage
  - 1}&size=${ITEMS_PER_PAGE}&sort=ranking,asc`)
  .then(response => response.json())
  .then(data => {
    console.log('Fetched data:', data);

    if (data && data.data) {
      // 페이지네이션 정보를 API 응답에서 가져옴
      allResults = data.data.ranks;
      totalPages = data.data.totalPages; // API 응답에서 총 페이지 수를 가져옴
      displayRanks(allResults); // 현재 페이지의 결과만 업데이트
      updatePagination(); // 페이지네이션 업데이트
    } else {
      console.error('Invalid data structure:', data);
      allResults = [];
      totalPages = 1;
      displayRanks(allResults); // 빈 결과 업데이트
      updatePagination(); // 페이지네이션 업데이트
    }
  })
  .catch(error => {
    console.error('Error fetching ranks:', error);
  });
}

// DOMContentLoaded 이벤트가 발생하면 필터링된 결과를 업데이트합니다.
document.addEventListener('DOMContentLoaded', () => {
  fetchAndDisplayRanks(); // 페이지 로드 시 데이터를 가져와서 표시
});

// 리뷰 아이템 호버 애니메이션
function applyHoverAnimationListeners() {
  const rankingItems = document.querySelectorAll('.ranking-item');
  if (rankingItems.length > 0) {
    rankingItems.forEach(item => {
      item.addEventListener('mouseenter', function () {
        this.style.transform = 'translateY(-5px)';
        this.style.boxShadow = '0 5px 15px rgba(0,0,0,0.1)';
      });
      item.addEventListener('mouseleave', function () {
        this.style.transform = 'translateY(0)';
        this.style.boxShadow = 'none';
      });
      item.addEventListener('click', function () {
        console.log('리뷰 상세 페이지로 이동:', this.querySelector('h3').textContent);
      });
    });
  }
}

// 좋아요 버튼 기능
function applyLikeButtonListeners() {
  const likeButtons = document.querySelectorAll('.like-button');
  if (likeButtons.length > 0) {
    likeButtons.forEach(button => {
      button.addEventListener('click', function (e) {
        e.stopPropagation();
        const campId = this.getAttribute('data-camp-id');
        const currentLikesCount = parseInt(this.getAttribute('data-likes-count'), 10);
        const newLikesCount = this.classList.contains('liked') ? currentLikesCount - 1 : currentLikesCount + 1;

        const method = this.classList.contains('liked') ? 'DELETE' : 'POST';
        const accessToken = localStorage.getItem('accessToken'); // 토큰 가져오기

        // 서버에 좋아요 추가/취소 요청을 보냅니다.
        fetch(`/api/camps/${campId}/like`, {
          method: method,
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${accessToken}` // 토큰 포함
          }
        })
        .then(response => {
          if (!response.ok) {
            throw new Error('Failed to update likes'); // 오류 발생 시 예외 처리
          }
          return response.json();
        })
        .then(data => {
          // 성공적으로 업데이트된 경우, UI에서 좋아요 수를 업데이트합니다.
          this.setAttribute('data-likes-count', newLikesCount);
          this.textContent = `❤ ${newLikesCount}`;
          this.classList.toggle('liked'); // 좋아요 상태 토글
        })
        .catch(error => {
          console.error('Error updating likes:', error);
        });
      });
    });
  }
}


// 페이지 이동 함수
function changePage(direction) {
  if (direction === -1 && currentPage > 1) {
    currentPage--;
  } else if (direction === 1 && currentPage < totalPages) {
    currentPage++;
  }
  fetchAndDisplayRanks();
}
