const { createApp, ref, reactive, computed, onBeforeUpdate} = Vue


createApp({
    setup() {
        let keyword = ref(''),
            totalItem = ref(0),
            groupedData = ref([]),
            totalPage = ref(0),
            currentIndex = ref(0),
            currentList = ref([]),
            isWaitingForData = ref(false),
            year = new Date().getFullYear().toString()

        const poemList = poem.split('\n').map(i=>i.trim()).filter(i=>i!=='')
        console.log(poemList)

        let randomPoem = () => poemList[Math.floor(Math.random() * poemList.length)],
            currentPoem = ref(randomPoem())
        // onBeforeUpdate(() => currentPoem.value = randomPoem())

        return {
            currentList,
            totalItem,
            totalPage,
            isWaitingForData,
            keyword,
            currentPoem,
            year,
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
                        let result = await query(keyword.value)
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
                            totalItem.value = result.totalItem
                            groupedData.value = result.groupedData
                            // errorMsg.value = result.errorMsg
                            totalPage.value = result.totalPage
                            currentList.value = groupedData.value[currentIndex.value]
                        }
                    } catch (e) {
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

async function query(keyword) {
    // console.log(111)
    return new Promise((resolve, reject) => {
        fetch('/search?submit=' + encodeURIComponent(keyword)).then(async(result) => {
            let resJson = await result.json()
            // console.log(resJson)
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



