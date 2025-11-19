// 로그아웃, 검색, 사용자 정보 가져오기, 메뉴 모달 열고 닫기
// 커뮤니티 버튼도 파고 이동 기능도 만들어야하지 않을까
let headerCurrentUserId = null;

const searchInput = document.getElementById('searchInput');
const searchButton = document.getElementById('searchButton');
const logoutBtn = document.querySelector('.logout-btn');

const profileBtn = document.getElementById('profileBtn');
const userDropdownMenu = document.getElementById('userDropdownMenu');
const userMenuLinks = document.getElementById('userMenuLinks'); // ul

document.addEventListener('DOMContentLoaded', function () {
    // 드랍다운 함수
    function toggleDropdown() {
        userDropdownMenu.classList.toggle('show');
        // 메뉴가 열릴 때마다 내용 업데이트 로직 재실행
        if (userDropdownMenu.classList.contains('show')) {
            updateMenuContent();
        }
    }

    // 외부 클릭 감지
    function closeDropdown(event) {
        // 프로필 버튼이나 드롭다운 메뉴를 클릭한 것이 아닐 경우 닫기
        if (userDropdownMenu && !userDropdownMenu.contains(event.target) && !profileBtn.contains(event.target)) {
            userDropdownMenu.classList.remove('show');
        }
    }

    // 로그인 상태에 따라 프로필 버튼 메뉴 변환
    function updateMenuContent() {
        // 로그인 여부 확인
        const isAuthenticated = headerCurrentUserId !== null;
        const authRequiredItem = userMenuLinks.querySelector('.user-menu-auth-required');
        const authenticatedItems = userMenuLinks.querySelectorAll('.user-menu-authenticated');

        if (isAuthenticated) {
            // 로그인 상태: 실제 링크 표시
            authRequiredItem.style.display = 'none';
            authenticatedItems.forEach(item => item.style.display = 'block');
        } else {
            // 비로그인 상태: 로그인 필요 메시지 표시
            authRequiredItem.style.display = 'block';
            authenticatedItems.forEach(item => item.style.display = 'none');
        }
    }

    // 검색 함수
    function performSearch() {
        if (searchInput && searchInput.value.trim()) {
            const keyword = searchInput.value.trim();
            window.location.href = `/products/search?keyword=${encodeURIComponent(keyword)}`;
        }
    }

    // 버튼에 기능 할당
    if (searchButton) {
        searchButton.addEventListener('click', performSearch);
    }

    // 엔터로 검색
    if (searchInput) {
        searchInput.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
                e.preventDefault(); // 기본 폼 방지
                performSearch();
            }
        });
    }

    // 사용자 링크 업데이트 및 id 가져오기
    async function fetchAndApplyUserLinks() {
        try {
            const response = await fetch('/api/users/me', {
                method: 'GET',
                // 브라우저 저장소에 헤더 있으면 추가
                // headers: { 'Authorization': `Bearer ${localStorage.getItem('accessToken')}` }
            });

            // 로그인 안되어있으면 null로 유지
            if (!response.ok || response.status === 204) {
                return;
            }

            const user = await response.json();
            headerCurrentUserId = user.id || user.userId;

            if (userMenuLinks && headerCurrentUserId) {
                console.log(`현재 사용자 id: ${headerCurrentUserId}`);
                const links = userMenuLinks.querySelectorAll('li[onclick*="location.href"]');

                links.forEach(li => {
                    let originalOnClick = li.getAttribute('onclick');
                    // 임시 ID '1'을 실제 headerCurrentUserId 대체
                    let newOnClick = originalOnClick.replace(/1/g, headerCurrentUserId);
                    li.setAttribute('onclick', newOnClick);
                });
            }

        } catch (error) {
            console.error("Failed to fetch user data or update links:", error);
        }
    }


    // 로그아웃 기능 할당
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            if (!confirm('정말 로그아웃 하시겠습니까?')) return;

            try {
                const res = await fetch('/api/logout', {
                    method: 'POST',
                    credentials: 'include'
                });

                if (res.ok) {
                    alert('로그아웃 완료!');
                    window.location.href = '/login';
                } else {
                    const text = await res.text();
                    alert('로그아웃 실패: ' + text);
                }
            } catch (err) {
                console.error('로그아웃 요청 실패:', err);
                alert('서버 오류가 발생했습니다.');
            }
        });
    }

    // 프로필 버튼에 함수 연결
    if (profileBtn) {
        profileBtn.addEventListener('click', toggleDropdown);
    }

    // ✅ [추가 필요] 외부 클릭 감지 시작
    document.addEventListener('click', closeDropdown);

    // 사용자 링크 업데이트 비동기 함수 실행 (가장 먼저 실행)
    fetchAndApplyUserLinks();
});