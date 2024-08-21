document.getElementById('reviewForm').addEventListener('submit', function(e) {
    e.preventDefault();

    // 폼 데이터 수집
    const selectedCategory = document.querySelector('input[name="category"]:checked')?.value;
    const selectedTrack = document.querySelector('input[name="track"]:checked')?.value;
    const reviewTitle = document.getElementById('review-title').value;
    const rating = document.querySelector('input[name="rating"]:checked')?.value;
    const reviewContent = document.getElementById('review-content').value;
    const subTitle = document.getElementById('sub-title').value;

    // 데이터 유효성 검증
    if (!selectedCategory || !selectedTrack || !reviewTitle || !rating
        || !reviewContent || !subTitle) {
        Swal.fire({
            title: '입력 오류',
            text: '모든 필드를 올바르게 입력해 주세요.',
            icon: 'error',
            confirmButtonText: '확인'
        });
        return;
    }

    // 서버로 전송할 데이터 준비
    const reviewData = {
        category: selectedCategory,
        trek: selectedTrack,
        title: reviewTitle,
        scope: parseInt(rating),  // 별점은 숫자형으로 변환
        content: reviewContent,
        campName: subTitle  // 캠프 이름으로 사용
    };

    fetch(`/api/reviews`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
        },
        body: JSON.stringify(reviewData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.statuscode === "201") {
            Swal.fire({
                title: '리뷰 작성 성공',
                text: '리뷰가 성공적으로 제출되었습니다!',
                icon: 'success',
                confirmButtonText: '확인'
            }).then(() => {
                if (data.data && data.data.id) {
                    const reviewId = data.data.id;
                    const detailUrl = `/review/${reviewId}`;
                    window.location.href = detailUrl;
                } else {
                    console.error('리뷰 ID가 반환되지 않았습니다.');
                }
            });
        } else {
            Swal.fire({
                title: '리뷰 작성 실패',
                text: data.msg || '리뷰 작성 중 오류가 발생했습니다.',
                icon: 'error',
                confirmButtonText: '확인'
            });
        }
    })
    .catch(error => {
        Swal.fire({
            title: '네트워크 오류',
            text: error.msg || '네트워크 오류가 발생했습니다.',
            icon: 'error',
            confirmButtonText: '확인'
        });
    });
});

function cancelReview() {
    if (confirm('작성 중인 리뷰를 취소하시겠습니까?')) {
        // 리뷰 작성 취소 후 리디렉션
        const cancelUrl = document.querySelector('.cancel-button').getAttribute('data-cancel-url');
        window.location.href = cancelUrl;
    }
}