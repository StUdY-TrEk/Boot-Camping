document.addEventListener('DOMContentLoaded', function() {
    const pathSegments = window.location.pathname.split('/');
    currentReviewId = pathSegments[pathSegments.length - 1]; // URL에서 리뷰 ID 추출

    if (currentReviewId) {
        fetchReviewDetails(currentReviewId);
        fetchComments(currentReviewId); // 댓글을 가져오는 함수 호출
    } else {
        console.error('리뷰 ID가 제공되지 않았습니다.');
    }

    addHeartButtonListeners();
});

// 리뷰 상세 정보를 가져오는 함수
function fetchReviewDetails(id) {
    fetch(`/api/reviews/${id}`)
    .then(response => response.json())
    .then(data => {
        if (data.statuscode === "200") {
            updateReviewDetails(data.data);
        } else {
            console.error('에러:', data.msg);
        }
    })
    .catch(error => console.error('API 호출 중 에러 발생:', error));
}

// 리뷰 상세 정보를 화면에 표시하는 함수
function updateReviewDetails(review) {
    document.querySelector('.review-title').textContent = review.title;
    document.querySelector('#subtitleDisplay').textContent = review.campName;
    const categories = document.querySelector('.meta-info div');
    categories.innerHTML = `<span class="category">${review.category}</span><span class="category">${review.trek}</span>`;
    document.querySelector('.date').textContent = new Date(review.createdAt).toLocaleDateString();
    document.querySelector('.rating').textContent = '★'.repeat(review.scope) + '☆'.repeat(5 - review.scope);
    document.querySelector('.detail-content').innerHTML = review.content.replace(/\n/g, '<br>');
    document.querySelector('.author').textContent = `- ${review.author || '익명'}`;

    // 수정 폼의 초기값 설정
    document.getElementById('editTitle').value = review.title;
    document.getElementById('editSubtitle').value = review.campName;
    document.getElementById('editCategory').value = review.category;
    document.getElementById('editDevCategory').value = review.trek;
    document.getElementById('editRating').value = review.scope;
    document.getElementById('editContent').value = review.content;
}

// 리뷰 수정 함수
function saveEdit() {
    if (confirm('정말 수정하시겠습니까?')) {
        const updatedReview = {
            title: document.getElementById('editTitle').value,
            campName: document.getElementById('editSubtitle').value,
            category: document.getElementById('editCategory').value,
            trek: document.getElementById('editDevCategory').value,
            scope: parseInt(document.getElementById('editRating').value, 10), // 별점 숫자로 변환
            content: document.getElementById('editContent').value
        };

        fetch(`/api/reviews/${currentReviewId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
            },
            body: JSON.stringify(updatedReview)
        })
        .then(response => response.json())
        .then(data => {
            if (data.statuscode === "200") {
                updateReviewDetails(data.data);
                document.getElementById('viewMode').style.display = 'block';
                document.getElementById('editMode').style.display = 'none';
                Swal.fire({
                    title: '수정 완료',
                    text: '리뷰가 성공적으로 수정되었습니다.',
                    icon: 'success',
                    confirmButtonText: '확인'
                });
            } else {
                console.error('에러:', data.msg);
                Swal.fire({
                    title: '수정 실패',
                    text: '리뷰 수정 중 오류가 발생했습니다.',
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
    }
}

// 기타 함수 (좋아요 버튼 리스너, 댓글 추가 등) 는 기존과 동일
document.addEventListener('DOMContentLoaded', function() {
    const pathSegments = window.location.pathname.split('/');
    currentReviewId = pathSegments[pathSegments.length - 1]; // URL에서 리뷰 ID 추출

    if (currentReviewId) {
        fetchReviewDetails(currentReviewId);
    } else {
        console.error('리뷰 ID가 제공되지 않았습니다.');
    }

    addHeartButtonListeners();
});

// 리뷰 상세 정보를 가져오는 함수
function fetchReviewDetails(id) {
    fetch(`/api/reviews/${id}`)
    .then(response => response.json())
    .then(data => {
        if (data.statuscode === "200") {
            updateReviewDetails(data.data);
        } else {
            console.error('에러:', data.msg);
        }
    })
    .catch(error => console.error('API 호출 중 에러 발생:', error));
}

// 리뷰 상세 정보를 화면에 표시하는 함수
function updateReviewDetails(review) {
    document.querySelector('.review-title').textContent = review.title;
    document.querySelector('#subtitleDisplay').textContent = review.campName;
    const categories = document.querySelector('.meta-info div');
    categories.innerHTML = `<span class="category">${review.category}</span><span class="category">${review.trek}</span>`;
    document.querySelector('.date').textContent = new Date(review.createdAt).toLocaleDateString();
    document.querySelector('.rating').textContent = '★'.repeat(review.scope) + '☆'.repeat(5 - review.scope);
    document.querySelector('.detail-content').innerHTML = review.content.replace(/\n/g, '<br>');
    document.querySelector('.author').textContent = `- ${review.author || '익명'}`;

    // 수정 폼의 초기값 설정
    document.getElementById('editTitle').value = review.title;
    document.getElementById('editSubtitle').value = review.campName;
    document.getElementById('editCategory').value = review.category;
    document.getElementById('editDevCategory').value = review.trek;
    document.getElementById('editRating').value = review.scope;
    document.getElementById('editContent').value = review.content;
}

// 리뷰 수정 함수
function saveEdit() {
    if (confirm('정말 수정하시겠습니까?')) {
        const updatedReview = {
            title: document.getElementById('editTitle').value,
            campName: document.getElementById('editSubtitle').value,
            category: document.getElementById('editCategory').value,
            trek: document.getElementById('editDevCategory').value,
            scope: parseInt(document.getElementById('editRating').value, 10), // 별점 숫자로 변환
            content: document.getElementById('editContent').value
        };

        fetch(`/api/reviews/${currentReviewId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
            },
            body: JSON.stringify(updatedReview)
        })
        .then(response => response.json())
        .then(data => {
            if (data.statuscode === "200") {
                updateReviewDetails(data.data);
                document.getElementById('viewMode').style.display = 'block';
                document.getElementById('editMode').style.display = 'none';
                Swal.fire({
                    title: '수정 완료',
                    text: '리뷰가 성공적으로 수정되었습니다.',
                    icon: 'success',
                    confirmButtonText: '확인'
                });
            } else {
                console.error('에러:', data.msg);
                Swal.fire({
                    title: '수정 실패',
                    text: '리뷰 수정 중 오류가 발생했습니다.',
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
    }
}

// 리뷰 삭제 함수
function reviewDel() {
    if (confirm('정말로 이 리뷰를 삭제하시겠습니까?')) {
        fetch(`/api/reviews/${currentReviewId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.statuscode === "204") { // 성공적으로 삭제됨
                Swal.fire({
                    title: '삭제 완료',
                    text: '리뷰가 성공적으로 삭제되었습니다.',
                    icon: 'success',
                    confirmButtonText: '확인'
                }).then(() => {
                    window.location.href = '/review'; // 리뷰 목록 페이지로 리디렉션
                });
            } else {
                console.error('에러:', data.msg);
                Swal.fire({
                    title: '삭제 실패',
                    text: '리뷰 삭제 중 오류가 발생했습니다.',
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
}

// 좋아요 버튼 리스너 추가 함수
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

// 댓글을 가져오는 함수
function fetchComments(reviewId) {
    fetch(`/api/reviews/${reviewId}/comments`)
    .then(response => response.json())
    .then(data => {
        if (data.statuscode === "200") {
            displayComments(data.data);
        } else {
            console.error('댓글 조회 에러:', data.msg);
        }
    })
    .catch(error => console.error('API 호출 중 에러 발생:', error));
}


// 댓글을 화면에 표시하는 함수
function displayComments(comments) {
    const commentsSection = document.querySelector('.comments-section');
    commentsSection.innerHTML = ''; // 기존 댓글 제거

    comments.forEach(comment => {
        const commentElement = document.createElement('div');
        commentElement.className = 'comment';
        commentElement.id = `comment${comment.id}`;
        commentElement.innerHTML = `
            <div class="comment-header">
                <div class="comment-author">${comment.author || '익명 사용자'}</div>
                <div class="comment-date">${new Date(comment.createdAt).toISOString().split('T')[0]}</div>
            </div>
            <div class="comment-content">${comment.content}</div>
            <div class="comment-actions">
                <button class="reply-button" onclick="showReplyForm('${comment.id}')">답글</button>
                <button class="edit-button" onclick="editComment('${comment.id}')">수정</button>
                <button class="delete-button" onclick="deleteComment('${comment.id}')">삭제</button>
            </div>
            <div class="reply-form" style="display: none;">
                <input type="text" placeholder="답글을 입력하세요...">
                <button onclick="addReply('${comment.id}')">답글 등록</button>
            </div>
            <div class="replies"></div>
        `;
        commentsSection.appendChild(commentElement);
        fetchReplies(currentReviewId, comment.id); // 각 댓글의 대댓글을 가져오는 함수 호출
    });
}

// 대댓글을 가져오는 함수
function fetchReplies(reviewId, commentId) {
    fetch(`/api/reviews/${reviewId}/comments/${commentId}/reply`)
    .then(response => response.json())
    .then(data => {
        if (data.statuscode === "200") {
            displayReplies(commentId, data.data);
        } else {
            console.error('대댓글 조회 에러:', data.msg);
        }
    })
    .catch(error => console.error('API 호출 중 에러 발생:', error));
}

// 대댓글을 화면에 표시하는 함수
function displayReplies(commentId, replies) {
    const repliesSection = document.querySelector(`#comment${commentId} .replies`);

    // 대댓글이 이미 존재하는지 확인하고 있으면 제거
    repliesSection.innerHTML = '';

    replies.forEach(reply => {
        const replyElement = document.createElement('div');
        replyElement.className = 'comment';
        replyElement.id = `reply${reply.id}`;
        replyElement.innerHTML = `
            <div class="comment-header">
                <div class="comment-author">${reply.author || '익명 사용자'}</div>
                <div class="comment-date">${new Date(reply.createdAt).toISOString().split('T')[0]}</div>
            </div>
            <div class="comment-content">${reply.content}</div>
            <div class="comment-actions">
                <button class="edit-button" onclick="editReply('${commentId}', '${reply.id}')">수정</button>
                <button class="delete-button" onclick="deleteReply('${commentId}', '${reply.id}')">삭제</button>
            </div>
        `;
        repliesSection.appendChild(replyElement);
    });
}



// 댓글 추가
function addComment() {
    const contentElement = document.getElementById('commentInput');
    const content = contentElement.value.trim(); // 입력된 내용의 공백을 제거

    if (!content) {
        Swal.fire({
            title: '댓글 추가 실패',
            text: '댓글 내용이 비어 있습니다.',
            icon: 'warning',
            confirmButtonText: '확인'
        });
        return;
    }

    fetch(`/api/reviews/${currentReviewId}/comments`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
        },
        body: JSON.stringify({ content })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('서버 응답이 올바르지 않습니다.');
        }
        return response.json();
    })
    .then(data => {
        if (data.statuscode === "201") {
            contentElement.value = ''; // 입력 필드 비우기
            fetchComments(currentReviewId); // 댓글 목록 갱신
            Swal.fire({
                title: '댓글 추가 완료',
                text: '댓글이 성공적으로 추가되었습니다.',
                icon: 'success',
                confirmButtonText: '확인'
            });
        } else {
            throw new Error(data.msg || '댓글 추가 중 오류가 발생했습니다.');
        }
    })
    .catch(error => {
        console.error('API 호출 중 에러 발생:', error);
        Swal.fire({
            title: '댓글 추가 실패',
            text: error.message,
            icon: 'error',
            confirmButtonText: '확인'
        });
    });
}


// 댓글 수정 함수
function editComment(commentId) {
    const commentElement = document.querySelector(`#comment${commentId}`);
    const content = prompt('댓글을 수정하세요:', commentElement.querySelector('.comment-content').textContent);

    if (content !== null) {
        fetch(`/api/reviews/${currentReviewId}/comments/${commentId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
            },
            body: JSON.stringify({ content })
        })
        .then(response => response.json())
        .then(data => {
            if (data.statuscode === "200") {
                fetchComments(currentReviewId); // 댓글 목록 갱신
                Swal.fire({
                    title: '댓글 수정 완료',
                    text: '댓글이 성공적으로 수정되었습니다.',
                    icon: 'success',
                    confirmButtonText: '확인'
                });
            } else {
                console.error('댓글 수정 에러:', data.msg);
                Swal.fire({
                    title: '댓글 수정 실패',
                    text: '댓글 수정 중 오류가 발생했습니다.',
                    icon: 'error',
                    confirmButtonText: '확인'
                });
            }
        })
        .catch(error => {
            console.error('API 호출 중 에러 발생:', error);
            Swal.fire({
                title: '댓글 수정 실패',
                text: '네트워크 오류가 발생했습니다.',
                icon: 'error',
                confirmButtonText: '확인'
            });
        });
    }
}


// 댓글 삭제 함수
function deleteComment(commentId) {
    if (confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
        fetch(`/api/reviews/${currentReviewId}/comments/${commentId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.statuscode === "204") {
                fetchComments(currentReviewId); // 댓글 목록 갱신
                Swal.fire({
                    title: '댓글 삭제 완료',
                    text: '댓글이 성공적으로 삭제되었습니다.',
                    icon: 'success',
                    confirmButtonText: '확인'
                });
            } else {
                console.error('댓글 삭제 에러:', data.msg);
                Swal.fire({
                    title: '댓글 삭제 실패',
                    text: '댓글 삭제 중 오류가 발생했습니다.',
                    icon: 'error',
                    confirmButtonText: '확인'
                });
            }
        })
        .catch(error => {
            console.error('API 호출 중 에러 발생:', error);
            Swal.fire({
                title: '댓글 삭제 실패',
                text: '네트워크 오류가 발생했습니다.',
                icon: 'error',
                confirmButtonText: '확인'
            });
        });
    }
}


// 답글 폼을 표시하는 함수
function showReplyForm(commentId) {
    const form = document.querySelector(`#comment${commentId} .reply-form`);
    form.style.display = 'block';
}


// 대댓글 추가 함수
function addReply(commentId) {
    const replyInput = document.querySelector(`#comment${commentId} .reply-form input`);
    const replyContent = replyInput.value.trim(); // 입력 내용의 공백 제거

    // 입력 내용이 비어있는 경우
    if (!replyContent) {
        Swal.fire({
            title: '대댓글 추가 실패',
            text: '대댓글 내용이 비어있습니다.',
            icon: 'warning',
            confirmButtonText: '확인'
        });
        return;
    }

    fetch(`/api/reviews/${currentReviewId}/comments/${commentId}/reply`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
        },
        body: JSON.stringify({ content: replyContent })
    })
    .then(response => response.json())
    .then(data => {
        if (data.statuscode === "201") {
            replyInput.value = ''; // 입력 필드 비우기
            fetchReplies(currentReviewId, commentId); // 대댓글 목록 갱신
            Swal.fire({
                title: '대댓글 추가 완료',
                text: '대댓글이 성공적으로 추가되었습니다.',
                icon: 'success',
                confirmButtonText: '확인'
            });
        } else {
            throw new Error(data.msg || '대댓글 추가 중 오류가 발생했습니다.');
        }
    })
    .catch(error => {
        console.error('API 호출 중 에러 발생:', error);
        Swal.fire({
            title: '대댓글 추가 실패',
            text: error.message,
            icon: 'error',
            confirmButtonText: '확인'
        });
    });
}



// 대댓글 수정 함수
function editReply(commentId, replyId) {
    const replyElement = document.querySelector(`#reply${replyId}`);

    if (!replyElement) {
        console.error('대댓글 요소를 찾을 수 없습니다:', `#reply${replyId}`);
        return;
    }

    const content = prompt('대댓글을 수정하세요:', replyElement.querySelector('.comment-content').textContent);

    if (content !== null) {
        fetch(`/api/reviews/${currentReviewId}/comments/${commentId}/reply/${replyId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
            },
            body: JSON.stringify({ content })
        })
        .then(response => response.json())
        .then(data => {
            if (data.statuscode === "200") {
                fetchReplies(currentReviewId, commentId); // 대댓글 목록 갱신
                Swal.fire({
                    title: '대댓글 수정 완료',
                    text: '대댓글이 성공적으로 수정되었습니다.',
                    icon: 'success',
                    confirmButtonText: '확인'
                });
            } else {
                console.error('대댓글 수정 에러:', data.msg);
                Swal.fire({
                    title: '대댓글 수정 실패',
                    text: '대댓글 수정 중 오류가 발생했습니다.',
                    icon: 'error',
                    confirmButtonText: '확인'
                });
            }
        })
        .catch(error => {
            console.error('API 호출 중 에러 발생:', error);
            Swal.fire({
                title: '대댓글 수정 실패',
                text: '네트워크 오류가 발생했습니다.',
                icon: 'error',
                confirmButtonText: '확인'
            });
        });
    }
}

// 대댓글 삭제 함수
function deleteReply(commentId, replyId) {
    if (!replyId) {
        console.error('대댓글 ID가 제공되지 않았습니다.');
        Swal.fire({
            title: '대댓글 삭제 실패',
            text: '대댓글 ID가 누락되었습니다.',
            icon: 'error',
            confirmButtonText: '확인'
        });
        return;
    }

    if (confirm('정말로 이 대댓글을 삭제하시겠습니까?')) {
        fetch(`/api/reviews/${currentReviewId}/comments/${commentId}/reply/${replyId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('accessToken')}` // Access Token 추가
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.statuscode === "204") {
                fetchReplies(currentReviewId, commentId); // 대댓글 목록 갱신
                Swal.fire({
                    title: '대댓글 삭제 완료',
                    text: '대댓글이 성공적으로 삭제되었습니다.',
                    icon: 'success',
                    confirmButtonText: '확인'
                });
            } else {
                throw new Error(data.msg || '대댓글 삭제 중 오류가 발생했습니다.');
            }
        })
        .catch(error => {
            console.error('API 호출 중 에러 발생:', error);
            Swal.fire({
                title: '대댓글 삭제 실패',
                text: error.message,
                icon: 'error',
                confirmButtonText: '확인'
            });
        });
    }
}






// 수정 폼 보기 함수
function showEditForm() {
    document.getElementById('viewMode').style.display = 'none';
    document.getElementById('editMode').style.display = 'block';
}

// 수정 취소 함수
function cancelEdit() {
    if (confirm('수정을 취소하시겠습니까? 변경사항이 저장되지 않습니다.')) {
        document.getElementById('viewMode').style.display = 'block';
        document.getElementById('editMode').style.display = 'none';
    }
}