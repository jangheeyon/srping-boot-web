// js/apiRequest.js
export async function apiRequest(url, options = {}) {
    let accessToken = localStorage.getItem("accessToken");
    const refreshToken = localStorage.getItem("refreshToken");

    // Authorization 헤더 자동 추가
    options.headers = {
        ...options.headers,
        "Content-Type": "application/json",
        "Authorization": accessToken ? `Bearer ${accessToken}` : undefined
    };

    let response = await fetch(url, options);

    // AccessToken 만료 시(401 또는 403) -> RefreshToken 요청
    if (response.status === 401 || response.status === 403) {
        if (!refreshToken) {
            alert("로그인이 필요합니다.");
            window.location.href = "/index.html";
            return;
        }

        const refreshResponse = await fetch("/refresh", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken })
        });

        if (!refreshResponse.ok) {
            alert("세션이 만료되었습니다. 다시 로그인 해주세요.");
            localStorage.removeItem("accessToken");
            localStorage.removeItem("refreshToken");
            window.location.href = "/index.html";
            return;
        }

        const data = await refreshResponse.json();
        localStorage.setItem("accessToken", data.accessToken);
        accessToken = data.accessToken;

        // 갱신된 토큰으로 원래 요청 재시도
        options.headers["Authorization"] = `Bearer ${accessToken}`;
        response = await fetch(url, options);
    }

    return response;
}

// JSON 응답 전용 헬퍼
export async function apiRequestJson(url, options = {}) {
    const response = await apiRequest(url, options);
    if (!response.ok) {
        throw new Error(`API 요청 실패: ${response.status}`);
    }
    return await response.json();
}
