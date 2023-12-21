// 动态年份
var yearSpan = document.getElementById("year");
yearSpan.innerText = new Date().getFullYear();

const { createApp, ref, reactive, computed } = Vue;

createApp({
    setup() {
        let keyword = ref('')
        let totalItem = ref(0),
            groupedData = ref([]),
            // errorMsg = ref(null),
            totalPage = ref(0)

        let currentIndex = ref(0)
        let currentList = ref([])
        let isWaitingForData = ref(false)

        return {
            currentList,
            totalItem,
            totalPage,
            isWaitingForData,
            keyword,
            currentPage: computed(() => currentIndex.value + 1),
            previousPage() {
                if (this.hasPreviousPage()) {
                    currentList.value = groupedData.value[--currentIndex.value]
                }
            },
            nextPage() {
                if (this.hasNextPage()) {
                    currentList.value = groupedData.value[++currentIndex.value]
                }
            },
            hasNextPage() {
                return currentIndex.value < totalPage.value - 1
            },
            hasPreviousPage() {
                return currentIndex.value > 0
            },
            async onQueryClick () {
                if (keyword.value == null || keyword.value.trim() === '') {
                    mdui.snackbar({
                        message: '请输入关键词!',
                        position: 'top'
                    });
                    keyword.value = ''
                } else {
                    isWaitingForData.value = true
                    try {
                        let result = await query(keyword.value)
                        if (result.totalItem === 0) {
                            mdui.dialog({
                                title: '小恶魔的温馨提示',
                                content: '没有在图书馆找到有关条目哦~',
                                buttons: [
                                    {
                                        text: '我知道了'
                                    }
                                ]
                            })
                            keyword.value = ''
                        } else {
                            totalItem.value = result.totalItem
                            groupedData.value = result.groupedData
                            // errorMsg.value = result.errorMsg
                            totalPage.value = result.totalPage
                            currentList.value = groupedData.value[currentIndex.value]
                        }
                    } catch (e) {
                        mdui.dialog({
                            title: '检索错误',
                            content: e,
                            buttons: [
                                {
                                    text: '确认'
                                }
                            ]
                        });
                    } finally {
                        isWaitingForData.value = false
                    }
                }
            }
        }
    }
}).mount('#baka-app');

async function query(keyword) {
    console.log(111)
    return new Promise((resolve, reject) => {
        fetch('/a?submit=' + encodeURIComponent(keyword)).then(async(result) => {
            let resJson = await result.json()
            console.log(resJson)
            if (resJson.errorMsg === undefined) {
                const data = resJson.data
                const totalPage = Math.ceil(data.length / 15)
                const groupedData = []
                let maxItemPerPage = 15
                for (let i = 0; i < totalPage; i++) {
                    let group = []
                    for (let j = i * maxItemPerPage; j < (i + 1) * maxItemPerPage; j++) {
                        if (j < data.length) {
                            group.push(data[j])
                        } else break
                    }
                    groupedData.push(group)
                }

                resolve({
                    groupedData,
                    totalPage,
                    totalItem: data.length,
                })
            } else reject("服务器错误: " + resJson.errorMsg)
        }).catch((e) => reject("获取数据的时候发生错误 " + e))
    })
}

