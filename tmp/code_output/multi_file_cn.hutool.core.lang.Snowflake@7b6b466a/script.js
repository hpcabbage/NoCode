// JavaScript 用于交互功能

// 平滑滚动到锚点
const navLinks = document.querySelectorAll('nav a');

navLinks.forEach(link => {
    link.addEventListener('click', function(e) {
        e.preventDefault();
        const targetId = this.getAttribute('href').substring(1);
        const targetSection = document.getElementById(targetId);
        if (targetSection) {
            targetSection.scrollIntoView({ behavior: 'smooth' });
        }
    });
});

// 按钮点击事件：显示提示信息
const joinButton = document.getElementById('joinButton');
if (joinButton) {
    joinButton.addEventListener('click', function() {
        alert('欢迎访问 ACM 官方网站了解更多信息！');
    });
}