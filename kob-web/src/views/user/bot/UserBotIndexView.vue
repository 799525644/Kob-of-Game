<template>
    <div class="container">
        <div class="row">
            <!-- 左边3区域 -->
            <div class="col-3">
                <div class="card" style="margin-top: 20px">
                    <!-- 卡片内容块 -->
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%;">
                    </div>
                </div>
            </div>
            <!-- 右边9区域 -->
            
            <div class="col-9">
                <div class="card" style="margin-top: 20px;">
                    <div class="card-header">
                        <span style="font-size: 130%">我的Bot</span>
                        <!-- 用button的data-bs-target="#NAME"和目标modal的id="NAME" -->
                        <button type="button" class="btn btn-primary float-end" 
                            data-bs-toggle="modal" data-bs-target="#add-bot-btn">
                            创建Bot</button>


                        <!-- Modal区 放在任意地方都可以-->
                        <!--  Modal 原型：https://getbootstrap.com/docs/5.3/components/modal/#modal-components-->
                      <!-- 增加Modal -->
                        <div class="modal fade" id="add-bot-btn" tabindex="-1" >
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title">创建Bot</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <form>
                                            <div class="mb-3">
                                                <label for="add-bot-title" class="form-label">名称</label>
                                                <input v-model="botaddmsg.title" type="text" class="form-control" id="add-bot-title" placeholder="请输入Bot名称">
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-description" class="form-label">简介</label>
                                                <textarea v-model="botaddmsg.description" class="form-control" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-code" class="form-label">代码</label>
                                                <!-- <textarea v-model="botaddmsg.content" class="form-control" id="add-bot-code" rows="7" placeholder="请编写Bot代码"></textarea> -->
                                                <VAceEditor
                                                    v-model:value="botaddmsg.content"
                                                    @init="editorInit"
                                                    lang="html"
                                                    style="height: 300px; font-size: large" 
                                                    :theme="aceConfig.theme"
                                                    :options="aceConfig.options" 
                                                    class="ace-editor"
                                                    />
                                                    
                                            </div>
                                        </form>
                                    </div>
                                    <div class="modal-footer">
                                        <div class="error-message">{{ botaddmsg.error_message }}</div>
                                        <button type="button" class="btn btn-primary" 
                                            @click="add_bot">创建</button>
                                        <button type="button" class="btn btn-secondary" 
                                            data-bs-dismiss="modal" >取消</button>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- 表格区 -->
                        <div class="card-body">
                            <table class="table table-striped table-hover">
                                <!-- 表头 第一行内容 -->
                                <thead>
                                    <tr>
                                        <th>名称</th>
                                        <th>创建时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <!-- v-for循环写法每个一行 -->
                                <tbody>
                                    <tr v-for="bot in bots" :key="bot.id">
                                        <td>{{ bot.title }}</td>
                                        <td>{{ bot.createtime }}</td>
                                        <td>
                                            <!-- 修改需要 -->
                                            <button type="button" class="btn btn-secondary" style="margin-right: 10px;"
                                                data-bs-toggle="modal"
                                                :data-bs-target="'#update-bot-modal-' + bot.id"
                                                >
                                                修改
                                            </button>

                                            <!-- 修改Modal -->
                                            <div class="modal fade" :id="`update-bot-modal-` + bot.id" tabindex="-1">
                                                <div class="modal-dialog modal-xl">
                                                    <div class="modal-content">
                                                        <div class="modal-header">
                                                            <h5 class="modal-title">修改Bot</h5>
                                                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                                        </div>
                                                        <div class="modal-body">
                                                            <form>
                                                                <div class="mb-3">
                                                                    <label for="update-bot-title" class="form-label">名称</label>
                                                                    <input v-model="bot.title" type="text" class="form-control" id="update-bot-title" 
                                                                        placeholder="请输入Bot名称" >
                                                                </div>
                                                                <div class="mb-3">
                                                                    <label for="update-bot-description" class="form-label">简介</label>
                                                                    <textarea v-model="bot.description" class="form-control" id="update-bot-description" 
                                                                        rows="3"  placeholder="请输入Bot简介"></textarea>
                                                                </div>
                                                                <div class="mb-3">
                                                                    <label for="update-bot-code" class="form-label">代码</label>
                                                                    <!-- <textarea v-model="bot.content" class="form-control" id="update-bot-code" rows="7"></textarea> -->
                                                                    <VAceEditor
                                                                        v-model:value="bot.content"
                                                                        @init="editorInit"
                                                                        lang="html"
                                                                        :theme="aceConfig.theme"
                                                                        style="height: 300px" 
                                                                        :options="aceConfig.options" class="ace-editor"
                                                                        />
                                                                </div>
                                                            </form>
                                                        </div>
                                                        <div class="modal-footer">
                                                            <div class="error-message">{{ bot.error_message }}</div>
                                                            <button type="button" class="btn btn-primary" 
                                                                @click="update_bot(bot)">保存修改</button>
                                                            <button type="button" class="btn btn-secondary" 
                                                                data-bs-dismiss="modal" >取消</button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <!-- 删除传入该bot即可 -->
                                            <button type="button" class="btn btn-danger"
                                                @click="remove_bot(bot)">
                                                删除
                                            </button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</template>

<script>
import { ref, reactive} from 'vue';
import {useStore} from 'vuex';
import {Modal} from 'bootstrap/dist/js/bootstrap';
import { VAceEditor } from 'vue3-ace-editor';
import $ from 'jquery';
export default {
    components: {
        VAceEditor
    },
    data(){
    },
    setup() {
        const store = useStore();
        let bots = ref([]);

        // VAce属性配置
        const aceConfig = reactive({
            theme: 'chrome', //主题
            arr: [
                /*所有主题*/
                "ambiance",
                "chaos",
                "chrome",
                "clouds",
                "clouds_midnight",
                "cobalt",
                "crimson_editor",
                "dawn",
                "dracula",
                "dreamweaver",
                "eclipse",
                "github",
                "gob",
                "gruvbox",
                "idle_fingers",
                "iplastic",
                "katzenmilch",
                "kr_theme",
                "kuroir",
                "merbivore",
                "merbivore_soft",
                "monokai",
                "mono_industrial",
                "pastel_on_dark",
                "solarized_dark",
                "solarized_light",
                "sqlserver",
                "terminal",
                "textmate",
                "tomorrow",
                "tomorrow_night",
                "tomorrow_night_blue",
                "tomorrow_night_bright",
                "tomorrow_night_eighties",
                "twilight",
                "vibrant_ink",
                "xcode",
            ],
            readOnly: false, //是否只读
            options: {
                enableBasicAutocompletion: true,
                enableSnippets: true,
                enableLiveAutocompletion: true,
                tabSize: 2,
                showPrintMargin: false,
                fontSize: 16
            }
        });


        const botaddmsg = reactive({
            title:"",
            description:"",
            content:"",
            error_message:"",
        });
        // 从接口取出bots数据
        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3001/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                    console.log("refresh_resp:",resp);
                }
            })
        }
        refresh_bots();
        const clean_reactive = (item) =>{
            item.title = "";
            item.content = "";
            item.description = "";
        }
        // 添加bot
        const add_bot = () => {
            botaddmsg.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3001/user/bot/add/",
                type: "POST",
                data:{
                    title: botaddmsg.title,
                    description: botaddmsg.description,
                    content: botaddmsg.content,
                },
                headers:{
                    Authorization: "Bearer "+ store.state.user.token
                },
                success(resp){
                    console.log("add_bot_resp:",resp);
                    if(resp.error_message === "success"){
                        refresh_bots();
                        clean_reactive(botaddmsg);
                        Modal.getInstance("#add-bot-btn").hide();
                    }else{
                        botaddmsg.error_message = resp.error_message;
                    }
                }
                
            })

        }

        // 删除bot
        const remove_bot = (bot) =>{
            $.ajax({
                url: "http://127.0.0.1:3001/user/bot/remove/",
                type: "POST",
                data:{
                    bot_id: bot.id,
                },
                headers:{
                    Authorization: "Bearer "+ store.state.user.token
                },
                success(resp){
                    console.log("remove_bot_resp:",resp);
                    if(resp.error_message === "success"){
                        refresh_bots();
                    }
                }
                
            })
        }
        // 修改bot
        const update_bot = (bot) => {
            bot.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3001/user/bot/update/",
                type: "POST",
                data:{
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers:{
                    Authorization: "Bearer "+ store.state.user.token
                },
                success(resp){
                    console.log("update_bot_resp:",resp);
                    if(resp.error_message === "success"){
                        Modal.getInstance(`#update-bot-btn`+bot.id).hide();
                        refresh_bots();
                    }else{
                        bot.error_message = resp.error_message;
                    }
                }
            })
        }



        // editor变量补充
        const dataForm = reactive({
            textareashow: '{"A":"A1"}'
        });

        const editorInit = () => {
            try {
                dataForm.textareashow = JSON.stringify(JSON.parse(dataForm.textareashow), null, 2)
            } catch (e) {
                console.log(`JSON字符串错误：${e.message}`);
            }
        };


        return {
            bots,
            botaddmsg,
            add_bot,
            update_bot,
            remove_bot,
            editorInit,
            aceConfig,
        }
    }
}
</script>
    
<style scoped>
div.error-message {
    color: red;
}
</style>