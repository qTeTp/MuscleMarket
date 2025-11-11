const searchInput = document.getElementById('searchInput');
const searchButton = document.getElementById('searchButton');

// 검색 실행 함수
function performSearch() {
    const keyword = searchInput.value.trim();
    if (keyword) {
        // ✅ /products/search 경로로 키워드를 쿼리 파라미터로 추가하여 이동
        window.location.href = `/products/search?keyword=${encodeURIComponent(keyword)}`;
    }
}
// 버튼에 검색
searchButton.addEventListener('click', performSearch);

// 엔터로 검색
searchInput.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        e.preventDefault(); // 기본 폼 제출 방지
        performSearch();
    }
});
