// 获取显示区域元素
const display = document.getElementById('display');

// 向显示区域添加内容
function appendToDisplay(value) {
    display.value += value;
}

// 清除显示区域
function clearDisplay() {
    display.value = '';
}

// 删除最后一个字符
function backspace() {
    display.value = display.value.slice(0, -1);
}

// 执行计算
function calculate() {
    try {
        // 安全地评估数学表达式
        const result = eval(display.value);
        
        // 检查结果是否为有限数字
        if (isFinite(result)) {
            display.value = result;
        } else {
            display.value = '错误';
        }
    } catch (error) {
        display.value = '错误';
    }
}

// 添加键盘支持
document.addEventListener('keydown', function(event) {
    // 允许的数字和运算符
    if (/[0-9+\-*/.=]/.test(event.key)) {
        event.preventDefault();
        if (event.key === '=' || event.key === 'Enter') {
            calculate();
        } else {
            appendToDisplay(event.key);
        }
    }
    
    // 退格键
    if (event.key === 'Backspace') {
        event.preventDefault();
        backspace();
    }
    
    // 清除键 (Esc 或 Delete)
    if (event.key === 'Escape' || event.key === 'Delete') {
        event.preventDefault();
        clearDisplay();
    }
});