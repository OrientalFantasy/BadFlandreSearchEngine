// 动态年份
var yearSpan = document.getElementById("year");
yearSpan.innerText = new Date().getFullYear();

const { createApp, ref } = Vue;

createApp({
    setup() {
        const message = ref('Hello vue!');
        const data = [
            {
                title: "Brunch this weekend?",
                url: "https://baidu.com",
                content: "All Connors - I'll be in your neighborhood doing errands this weekend. Do you want ..."
            },
            {
                title: "casca Brunch this weekend?",
                url: "https://qq.com",
                content: "casasca neighborhood doing errands this weekend. Do you want "
            },
            {
                title: "casca Brunch this weekend?",
                url: "https://qq.com",
                content: "casasca neighborhood doing errands this weekend. Do you want "
            },
            {
                title: "casca Brunch this weekend?",
                url: "https://qq.com",
                content: "casasca neighborhood doing errands this weekend. Do you want "
            },
            {
                title: "casca Brunch this weekend?",
                url: "https://qq.com",
                content: "casasca neighborhood doing errands this weekend. Do you want "
            },
            {
                title: "casca Brunch this weekend?",
                url: "https://qq.com",
                content: "casasca neighborhood doing errands this weekend. Do you want "
            }
        ]
        return {
            message,
            data
        }
    }
}).mount('#baka-app');


function jumpTo(url) {
    window.location.href = url
}

