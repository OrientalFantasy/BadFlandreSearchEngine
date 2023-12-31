// 引入Vue组件
const { createApp, ref, reactive, computed, onBeforeUpdate} = Vue

// 创建Vue应用
createApp({
    // 初始化
    setup() {
        // 声明变量
            // 搜索关键词
        let keyword = ref(''),
            // 共搜索到的条目数量
            totalItem = ref(0),
            // 分组后端返回的数据
            groupedData = ref([]),
            // 总页数
            totalPage = ref(0),
            // 当前页数的索引
            currentIndex = ref(0),
            // 当前显示页的数据
            currentList = ref([]),
            // 是否在等待后端返回数据
            isWaitingForData = ref(false),
            // 获取年份
            year = new Date().getFullYear().toString()
        // 一言数据切片
        const poemList = poem.split('\n').map(i=>i.trim()).filter(i=>i!=='')
        // 随机选取一条一言句子
        let randomPoem = () => poemList[Math.floor(Math.random() * poemList.length)],
            currentPoem = ref(randomPoem())
        // onBeforeUpdate(() => currentPoem.value = randomPoem())

        const reset = () => {
            totalItem.value = 0
            groupedData.value = []
            totalPage.value = 0
            currentList.value = []
            currentIndex.value = 0
        }

        return {
            currentList,
            totalItem,
            totalPage,
            isWaitingForData,
            keyword,
            currentPoem,
            year,
            // 当前页数
            currentPage: computed(() => currentIndex.value + 1),
            // 上一页函数
            previousPage() {
                if (this.hasPreviousPage()) {
                    currentList.value = groupedData.value[--currentIndex.value]
                }
            },
            // 下一页函数
            nextPage() {
                if (this.hasNextPage()) {
                    currentList.value = groupedData.value[++currentIndex.value]
                }
            },
            // 判断是否有下一页
            hasNextPage() {
                return currentIndex.value < totalPage.value - 1
            },
            // 判断是否有上一页
            hasPreviousPage() {
                return currentIndex.value > 0
            },
            // 发起搜索请求
            async onQueryClick () {
                // 判断搜索关键词是否为空
                if (keyword.value == null || keyword.value.trim() === '') {
                     mdui.dialog({
                        title: '<font style="color:red;">小恶魔</font>',
                        content: '<font style="color:red;">请给我要检索的信息哦~</font>',
                        buttons: [
                            {
                                text: '我知道了'
                            }
                        ]
                    })
                    keyword.value = ''
                } else {
                    isWaitingForData.value = true
                    try {
                        // 发起请求
                        let result = await query(keyword.value)
                        // 结果判断
                        if (result.totalItem === 0) {
                            mdui.dialog({
                                title: '<font style="color:red;">小恶魔</font>',
                                content: '<font style="color:red;">伏瓦鲁魔法图书馆暂未收录有关条目哦~</font>',
                                buttons: [
                                    {
                                        text: '我知道了'
                                    }
                                ]
                            })
                            keyword.value = ''
                        } else {
                            reset()
                            // 更新页面数据
                            totalItem.value = result.totalItem
                            groupedData.value = result.groupedData
                            totalPage.value = result.totalPage
                            currentList.value = groupedData.value[currentIndex.value]
                        }
                    } catch (e) {
                        // 判断是否超时或者遭遇其他错误
                        mdui.dialog({
                            title: '<font style="color:red;">小恶魔</font>',
                            content: '<font style="color:red;">遭遇了奇怪的异变！以下是或许有些帮助的神秘提示！</font><br>' + e,
                            buttons: [
                                {
                                    text: '我知道了'
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

// 请求函数
async function query(keyword) {
    return new Promise((resolve, reject) => {
        fetch('/search?submit=' + encodeURIComponent(keyword)).then(async(result) => {
            let resJson = await result.json()
            if (resJson.errorMsg === undefined) {
                const data = resJson.data
                // 根据后端返回数据条数计算页数
                const totalPage = Math.ceil(data.length / 15)
                const groupedData = []
                let maxItemPerPage = 15
                // 给后端返回的数据进行分组
                for (let i = 0; i < totalPage; i++) {
                    let group = []
                    for (let j = i * maxItemPerPage; j < (i + 1) * maxItemPerPage; j++) {
                        if (j < data.length) {
                            group.push(data[j])
                        } else break
                    }
                    groupedData.push(group)
                }
                // 返回数据
                resolve({
                    groupedData,
                    totalPage,
                    totalItem: data.length,
                })
            } else reject("服务器错误: " + resJson.errorMsg)
        }).catch((e) => reject("获取数据的时候发生错误 " + e))
    })
}



