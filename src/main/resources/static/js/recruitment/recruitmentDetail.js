const courseContainer = document.getElementById('courseContainer');
const editButton = document.getElementById('editButton');
const saveButton = document.getElementById('saveButton');
const cancelButton = document.getElementById('cancelButton');
const deleteButton = document.getElementById('deleteButton');
const likeButton = document.getElementById('likeButton');
const likeCount = document.getElementById('likeCount');
const classTypeSelect = document.getElementById('classTypeSelect');
const costTypeSelect = document.getElementById('costTypeSelect');
const fieldTypeSelect = document.getElementById('fieldTypeSelect');
const selectedCategories = document.getElementById('selectedCategories');
const imageUpload = document.getElementById('imageUpload');
const reviewSummary = document.getElementById('reviewSummary');

let originalContent = {};
let selectedCategoryList = {
  classType: '',
  costType: '',
  fieldType: ''
};

document.addEventListener('DOMContentLoaded', function() {
  const imageUpload = document.querySelector('#imageUpload'); // 통일된 ID 사용

  if (imageUpload) {
    imageUpload.addEventListener('change', function() {
      const file = imageUpload.files[0];
      if (file) {
        console.log('Image selected: ', file.name);
      }
    });
  } else {
    console.error('imageUpload 요소를 찾을 수 없습니다.');
  }
});

// Flatpickr 초기화
flatpickr(".date-range", {
  mode: "range",
  dateFormat: "Y-m-d",
  disable: [], // 주말 비활성화 설정 제거
  locale: {
    firstDayOfWeek: 6 // 토요일부터 시작
  }
});

// 모든 카테고리 항목을 가져오는 함수
function getAllTags() {
  return document.querySelectorAll('.course-tags .tag');
}

// 카테고리 태그를 업데이트하는 함수
function updateTagVisibility() {
  const tags = getAllTags();
  tags.forEach(tag => {
    const tagValue = tag.textContent.trim();
    if (Object.values(selectedCategoryList).includes(tagValue)) {
      tag.style.display = 'inline'; // 태그를 보이게 설정
    } else {
      tag.style.display = 'none'; // 태그를 숨김
    }
  });
}

// 선택된 카테고리를 업데이트하는 함수
function updateSelectedCategories() {
  selectedCategories.innerHTML = ''; // 기존 카테고리 제거
  for (const [key, value] of Object.entries(selectedCategoryList)) {
    if (value) {
      const span = document.createElement('span');
      span.className = 'selected-category';
      span.innerHTML = `${value} <span class="remove-category" data-category="${key}">✕</span>`;
      selectedCategories.appendChild(span);
    }
  }

  // 삭제 버튼에 이벤트 리스너 추가
  document.querySelectorAll('.remove-category').forEach(button => {
    button.addEventListener('click', function() {
      const categoryToRemove = this.getAttribute('data-category');
      selectedCategoryList[categoryToRemove] = '';
      updateSelectedCategories();
      updateTagVisibility();
      document.getElementById(`${categoryToRemove}Select`).value = '';
    });
  });
}

// Select 요소의 변경 이벤트 처리
[classTypeSelect, costTypeSelect, fieldTypeSelect].forEach(select => {
  select.addEventListener('change', function() {
    const categoryType = this.id.replace('Select', '');
    selectedCategoryList[categoryType] = this.value;
    updateSelectedCategories();
    updateTagVisibility();
  });
});

// 수정 버튼 클릭 시
editButton.addEventListener('click', function() {
  courseContainer.classList.remove('view-mode');
  courseContainer.classList.add('edit-mode');

  // 현재 내용 저장
  document.querySelectorAll('.editable').forEach(elem => {
    originalContent[elem.className] = elem.value;
  });

  // 현재 선택된 카테고리로 select 요소 업데이트
  for (const [key, value] of Object.entries(selectedCategoryList)) {
    const selectElem = document.getElementById(`${key}Select`);
    if (selectElem) {
      selectElem.value = value;
    }
  }

  updateSelectedCategories();
});

// 저장 버튼 클릭 시
saveButton.addEventListener('click', function() {
  // 캠프와 모집 기간을 각각 시작일과 종료일로 분리
  const [campStart, campEnd] = document.querySelector('.course-sidebar .editable.date-range').value.split(' to ');
  const [recruitStart, recruitEnd] = document.querySelector('.course-sidebar .editable.recruit').value.split(' to ');

  // FormData 객체 생성
  const recruitmentData = new FormData();
  // JSON 데이터를 FormData에 추가
  recruitmentData.append('data', new Blob([JSON.stringify({
    title: document.querySelector('.course-title.editable').value,
    campName: document.querySelector('.course-institution.editable').value,
    process: document.querySelector('.course-intro.editable').value,
    content: document.querySelector('.course-info .editable').value,
    place: classTypeSelect.value,
    cost: costTypeSelect.value,
    trek: fieldTypeSelect.value,
    level: document.querySelector('.course-sidebar select').value,
    classTime: document.querySelector('.editable.class-time').value,
    campStart: campStart,
    campEnd: campEnd,
    recruitStart: recruitStart,
    recruitEnd: recruitEnd
  })], { type: 'application/json' }));

  // 이미지 파일이 선택되었으면 FormData에 추가
  if (imageUpload.files[0]) {
    recruitmentData.append('imageFile', imageUpload.files[0]);
  }

  // API 호출
  fetch(`/api/camps/${recruitmentId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    },
    body: recruitmentData
  })
  .then(response => {
    // HTTP 응답 상태 확인
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
  })
  .then(data => {
    if (data.statuscode === '200') {
      Swal.fire({
        title: '수정 완료',
        text: '모집글이 성공적으로 수정되었습니다.',
        icon: 'success',
        confirmButtonText: '확인'
      }).then(() => {
        // 이미지 갱신 코드 추가
        if (data.data.newImageUrl) {
          const imageElement = document.getElementById('imageElementId'); // 실제 이미지 요소의 ID로 교체
          imageElement.src = data.data.newImageUrl; // 새로운 이미지 URL로 갱신
        }
        window.location.href = `/camp/${recruitmentId}`;
      });
    } else {
      Swal.fire({
        title: '수정 실패',
        text: `오류 발생: ${data.msg}`||'모집글 수정 중 오류가 발생했습니다.',
        icon: 'error',
        confirmButtonText: '확인'
      });
    }
  })
  .catch(error => {
    console.error('API 호출 중 에러 발생:', error);
    Swal.fire({
      title: '수정 실패',
      text: '네트워크 오류가 발생했습니다.',
      icon: 'error',
      confirmButtonText: '확인'
    });
  });
});


// 리뷰 요약을 업데이트하는 함수
function updateReviewSummary(summary) {
  const reviewSummaryText = document.getElementById('reviewSummary-text');
  if (reviewSummaryText && summary) {
    reviewSummaryText.textContent = summary;
  }
}

// 뷰 모드 업데이트
function updateViewMode(data) {
  // 제목 업데이트
  document.querySelector('.course-title.non-editable').textContent = data.title;
  document.querySelector('.course-title.editable').value = data.title;

  // 캠프 이름 업데이트
  document.querySelector('.course-institution.non-editable').textContent = data.campName;
  document.querySelector('.course-institution.editable').value = data.campName;

  // 과정 소개 업데이트
  document.querySelector('.course-intro.non-editable').textContent = data.process;
  document.querySelector('.course-intro.editable').textContent = data.process;

  // 과정 내용 업데이트
  document.querySelector('.course-info .non-editable').textContent = data.content;
  document.querySelector('.course-info .editable').value = data.content;

  // 참여 기간 업데이트
  document.querySelector('.course-sidebar .non-editable').textContent = `참여 기간: ${data.campStart} ~ ${data.campEnd}`;
  document.querySelector('.course-sidebar .editable.date-range').value = `${data.campStart} to ${data.campEnd}`;

  // 모집 기간 업데이트
  document.querySelector('.course-sidebar .non-editable.recruit').textContent = `모집 기간: ${data.recruitStart} ~ ${data.recruitEnd}`;
  document.querySelector('.course-sidebar .editable.recruit.date-range').value = `${data.recruitStart} to ${data.recruitEnd}`;

  // 수업 시간 업데이트
  document.querySelector('.non-editable.class-time').textContent = `수업 시간: ${data.classTime}`;
  document.querySelector('.editable.class-time').value = data.classTime;

  // 난이도 드롭다운 업데이트
  document.querySelector('.non-editable.level').textContent = `난이도: ${data.level}`;
  document.querySelector('.course-sidebar select').value = data.level;

  // 수업 방식 드롭다운 업데이트
  document.querySelector('#classTypeSelect').value = data.place;

  // 비용 드롭다운 업데이트
  document.querySelector('#costTypeSelect').value = data.cost;

  // 학습 분야 드롭다운 업데이트
  document.querySelector('#fieldTypeSelect').value = data.trek;

  // 리뷰 요약 업데이트 추가
  updateReviewSummary(data.summary);

  // 태그 업데이트
  let tags = data.tags || [];
  document.querySelectorAll('.course-tags .tag').forEach(tagElement => {
    tagElement.style.display = tags.includes(tagElement.textContent.trim()) ? 'inline' : 'none';
  });

  // 선택된 카테고리 업데이트
  selectedCategoryList = data.selectedCategoryList || {};
  updateSelectedCategories();
}

let recruitmentId = '';

// 페이지 로드 시 데이터 가져오기
document.addEventListener('DOMContentLoaded', function() {
  const urlParts = window.location.pathname.split('/');
  recruitmentId = urlParts[urlParts.length - 1];

  if (recruitmentId) {
    fetch(`/api/camps/${encodeURIComponent(recruitmentId)}`)
    .then(response => response.json())
    .then(data => {
      if (data.statuscode === '200') {
        updateViewMode(data.data);
      } else {
        alert('데이터를 불러오는데 실패했습니다: ' + data.msg);
      }
    })
    .catch(error => console.error('Error:', error));
  } else {
    console.error('Invalid recruitment ID');
  }
});

// URL 파라미터 제거
const url = new URL(window.location.href);
url.search = ''; // 모든 쿼리 파라미터 제거
window.history.replaceState({}, '', url);

// 카테고리 태그 업데이트
function updateTagContainer() {
  const tagContainer = document.querySelector('.course-tags');
  tagContainer.innerHTML = '';
  for (const value of Object.values(selectedCategoryList)) {
    if (value) {
      const tag = document.createElement('span');
      tag.className = 'tag';
      tag.textContent = value;
      tagContainer.appendChild(tag);
    }
  }
}
updateTagContainer();

// 취소 버튼 클릭 시
cancelButton.addEventListener('click', function() {
  courseContainer.classList.remove('edit-mode');
  courseContainer.classList.add('view-mode');

  // 원래 내용으로 복구
  document.querySelectorAll('.editable').forEach(elem => {
    elem.value = originalContent[elem.className];
  });

  // 카테고리 복구
  const tags = document.querySelectorAll('.course-tags .tag');
  selectedCategoryList = {
    classType: '',
    costType: '',
    fieldType: ''
  };
  tags.forEach(tag => {
    const text = tag.textContent.trim();
    if (['온라인', '오프라인'].includes(text)) {
      selectedCategoryList.classType = text;
    } else if (['국비', '유료', '무료'].includes(text)) {
      selectedCategoryList.costType = text;
    } else {
      selectedCategoryList.fieldType = text;
    }
  });
  updateSelectedCategories();
  updateTagVisibility();
});

// 삭제 버튼 클릭 시
deleteButton.addEventListener('click', function() {
  if (confirm('정말로 이 리뷰를 삭제하시겠습니까?')) {
    fetch(`/api/camps/${recruitmentId}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
      }
    })
    .then(response => response.json())
    .then(data => {
      if (data.statuscode === "204") {
        Swal.fire({
          title: '삭제 완료',
          text: '모집글이 성공적으로 삭제되었습니다.',
          icon: 'success',
          confirmButtonText: '확인'
        }).then(() => {
          window.location.href = '/camp';
        });
      } else {
        console.error('에러:', data.msg);
        Swal.fire({
          title: '삭제 실패',
          text: '모집글 삭제 중 오류가 발생했습니다.',
          icon: 'error',
          confirmButtonText: '확인'
        });
      }
    })
    .catch(error => {
      console.error('API 호출 중 에러 발생:', error);
      Swal.fire({
        title: '삭제 실패',
        text: '네트워크 오류가 발생했습니다.',
        icon: 'error',
        confirmButtonText: '확인'
      });
    });
  }
});

// 좋아요 버튼 기능
let isLiked = false;
let currentLikes = 1234;

likeButton.addEventListener('click', function() {
  isLiked = !isLiked;
  currentLikes += isLiked ? 1 : -1;
  likeButton.classList.toggle('active', isLiked);
  likeCount.textContent = currentLikes.toLocaleString();
});

// 관심 목록 추가 버튼 클릭 이벤트
document.querySelector('.wishlist-button').addEventListener('click', function() {
  alert('관심 목록에 추가되었습니다!');
});
