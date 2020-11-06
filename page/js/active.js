/**
 * 机器码生成激活码
 * @param {string} mcode 机器码
 */
function active(mcode) {
    // 分离出前和后
    let left = '0x' + mcode.substr(0, 8);
    let right = '0x' + mcode.substr(8, 8);

    // 解析成整数
    left = parseInt(left);
    right = parseInt(right);

    // 准备异或的16进制数
    hexLeft = 0xABCD9876;
    hexRight = 0x9876ABCD;

    // 异或运算
    left = xor(left, hexLeft);
    right = xor(right, hexRight);

    // 结果转成16进制
    left = left.toString(16);
    right = right.toString(16);

    // 每段补齐8位
    left = frontSpace(left, 8, '0');
    right = frontSpace(right, 8, '0');

    return (left + right).toUpperCase();
}

// 异或运算
function xor(a, b) {
    a = a.toString(2).split('').reverse();
    b = b.toString(2).split('').reverse();
    var L = Math.max(a.length, b.length), re = [];
    for (var i = 0; i < L; i++) {
        re.push(a[i] && b[i] ? (a[i] != b[i] ? 1 : 0) : (a[i] || b[i]))
    }
    return parseInt(re.reverse().join(''), 2);
}

// 前补空格
function frontSpace(str, num, fill) {
    if (!fill) {
        fill = ' ';
    }
    string = str;
    while (string.length < num) {
        string = fill + string;
    }
    return string;
}