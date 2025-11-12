// header.js
document.addEventListener('DOMContentLoaded', () => {
    const logoutBtn = document.querySelector('.logout-btn');
    if (!logoutBtn) {
        console.error("❌ logout-btn 버튼을 찾지 못했습니다.");
        return;
    }

    logoutBtn.addEventListener('click', async () => {
        if (!confirm('정말 로그아웃 하시겠습니까?')) return;

        try {
            const res = await fetch('/api/logout', {
                method: 'POST',
                credentials: 'include' // 쿠키 포함
            });

            if (res.ok) {
                alert('로그아웃 완료!');
                window.location.href = '/login'; // 원하는 페이지로 리디렉션
            } else {
                const text = await res.text();
                alert('로그아웃 실패: ' + text);
            }
        } catch (err) {
            console.error('🚨 로그아웃 요청 실패:', err);
            alert('서버 오류가 발생했습니다.');
        }
    });
});
