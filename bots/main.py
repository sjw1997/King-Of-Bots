import requests
import websocket
import json

class Player:
    username = ""
    password = ""
    id = 0
    jwt_token = ""
    bots_id = []
    headers = {}
    ws = None

    def __init__(self, username, password):
        self.username = username
        self.password = password
    
    def login(self):
        get_token_url = "https://app5212.acapp.acwing.com.cn/api/user/account/token/"
        data = {
            'username': self.username,
            'password': self.password,
        }
        response = requests.post(get_token_url, data=data)

        if response.status_code == 200:
            resp = response.json()
            if resp["error_message"] == "success":
                self.jwt_token = resp["token"]
                self.headers = {
                    'Authorization': "Bearer " + self.jwt_token
                }
                print("登录成功")
                self.get_user_id()
                self.get_bots()
                self.connect_ws()
            else:
                print("登录失败")
        else:
            print("连接失败")
    
    def get_user_id(self):
        get_user_id_url = "https://app5212.acapp.acwing.com.cn/api/user/account/info/"
        response = requests.get(get_user_id_url, headers=self.headers)
        if response.status_code == 200:
            resp = response.json()
            if resp["error_message"] == "success":
                self.id = resp["id"]
                print("获取user_id成功")
            else:
                print("获取user_id失败")
        else:
            print("连接失败")

    def get_bots(self):
        url = "https://app5212.acapp.acwing.com.cn/api/user/bot/getlist/"
        response = requests.get(url, headers=self.headers)
        if response.status_code == 200:
            bots = eval(response.text)
            for bot in bots:
                self.bots_id.append(bot['id'])
            print("获取bots_id成功")
        else:
            print("连接失败")

    def connect_ws(self):
        def on_message(ws, message):
            resp = json.loads(message)
            if resp["event"] == "start-matching":
                print("匹配成功")
            elif resp["event"] == "result":
                start_matching(ws)

        def on_open(ws):
            start_matching(ws)
        
        def start_matching(ws):
            data = {
                'event': 'start-matching',
                'bot_id': self.bots_id[0]
            }
            ws.send(json.dumps(data))
            print("发送匹配成功")
        
        url = "wss://app5212.acapp.acwing.com.cn/websocket/%s/" % (self.jwt_token)
        self.ws = websocket.WebSocketApp(url, on_message=on_message, on_open=on_open)
        self.ws.run_forever()


if __name__ == "__main__":
    json_text = ""
    with open("bot_config.json") as f:
        json_text = f.read()
    bot = json.loads(json_text)

    player_0 = Player(bot["username"], bot["password"])
    player_0.login()
