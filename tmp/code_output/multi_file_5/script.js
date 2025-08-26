// 导航菜单切换功能
const hamburger = document.querySelector('.hamburger');
const navMenu = document.querySelector('.nav-menu');

hamburger.addEventListener('click', () => {
    hamburger.classList.toggle('active');
    navMenu.classList.toggle('active');
});

// 点击导航链接关闭菜单
document.querySelectorAll('.nav-link').forEach(link => {
    link.addEventListener('click', () => {
        hamburger.classList.remove('active');
        navMenu.classList.remove('active');
    });
});

// 轮播图功能
document.addEventListener('DOMContentLoaded', function() {
    const slides = document.querySelectorAll('.carousel-slide');
    const indicators = document.querySelectorAll('.indicator');
    const prevButton = document.querySelector('.carousel-control.prev');
    const nextButton = document.querySelector('.carousel-control.next');
    let currentSlide = 0;
    let slideInterval;

    // 切换到指定幻灯片
    function goToSlide(n) {
        slides[currentSlide].classList.remove('active');
        indicators[currentSlide].classList.remove('active');
        
        currentSlide = (n + slides.length) % slides.length;
        
        slides[currentSlide].classList.add('active');
        indicators[currentSlide].classList.add('active');
    }

    // 下一张幻灯片
    function nextSlide() {
        goToSlide(currentSlide + 1);
    }

    // 开始自动轮播
    function startSlideShow() {
        slideInterval = setInterval(nextSlide, 5000);
    }

    // 停止自动轮播
    function stopSlideShow() {
        clearInterval(slideInterval);
    }

    // 事件监听器
    nextButton.addEventListener('click', () => {
        stopSlideShow();
        nextSlide();
        startSlideShow();
    });

    prevButton.addEventListener('click', () => {
        stopSlideShow();
        goToSlide(currentSlide - 1);
        startSlideShow();
    });

    // 指示器点击事件
    indicators.forEach((indicator, index) => {
        indicator.addEventListener('click', () => {
            stopSlideShow();
            goToSlide(index);
            startSlideShow();
        });
    });

    // 鼠标悬停时暂停轮播
    const carouselContainer = document.querySelector('.carousel-container');
    carouselContainer.addEventListener('mouseenter', stopSlideShow);
    carouselContainer.addEventListener('mouseleave', startSlideShow);

    // 触摸设备支持
    let touchStartX = 0;
    let touchEndX = 0;
    
    carouselContainer.addEventListener('touchstart', e => {
        touchStartX = e.changedTouches[0].screenX;
        stopSlideShow();
    }, false);
    
    carouselContainer.addEventListener('touchend', e => {
        touchEndX = e.changedTouches[0].screenX;
        handleSwipe();
        startSlideShow();
    }, false);
    
    function handleSwipe() {
        const minSwipeDistance = 50; // 最小滑动距离
        const swipeDistance = touchEndX - touchStartX;
        
        if (Math.abs(swipeDistance) < minSwipeDistance) return;
        
        if (swipeDistance > 0) {
            // 向右滑动，显示上一张
            goToSlide(currentSlide - 1);
        } else {
            // 向左滑动，显示下一张
            nextSlide();
        }
    }

    // 初始化轮播
    startSlideShow();
});