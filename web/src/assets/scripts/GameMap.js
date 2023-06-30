import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends AcGameObject {
    constructor(ctx, parent, timer, round, store) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;
        this.store = store;

        this.g = store.state.pk.gamemap;        
        this.rows = this.g.length;
        this.cols = this.g[0].length;

        this.walls = [];

        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this)
        ];

        this.timer = timer;
        this.schedule_time = 6000;
        this.time_left = this.schedule_time; // 单位：毫秒
        this.has_input = false;
        this.automatically_move = this.store.state.pk.my_bot_id !== -1;
    
        this.round = round;
        this.steps = 1;
    }
 
    create_walls() {        
        for (let r = 0; r < this.rows; r ++ ) {
            for (let c = 0; c < this.cols; c ++ ) {
                if (this.g[r][c]) {
                    this.walls.push(new Wall(r, c, this));
                }
            }
        }
    }

    add_listening_events() {
        if (this.store.state.record.is_record) {
            let k = 0;
            const a_steps = this.store.state.record.a_steps;
            const b_steps = this.store.state.record.b_steps;
            const length = a_steps.length;
            const [snakeA, snakeB] = this.snakes;
            const loser = this.store.state.record.record_loser;

            const interval_id = setInterval(() => {
                if (k >= length - 1) {
                    if (loser === "all") {
                        snakeA.status = snakeB.status = "die";
                    } else if (loser === "A") {
                        snakeA.status = "die";
                    } else if (loser === "B") {
                        snakeB.status = "die";
                    }
                    clearInterval(interval_id);
                } else {
                    snakeA.set_direction(parseInt(a_steps[k]));
                    snakeB.set_direction(parseInt(b_steps[k]));
                }
                k ++ ;
            }, 300);
        } else {
            this.ctx.canvas.focus();

            this.ctx.canvas.addEventListener("keydown", e => {
                let d = -1;
                if (e.key === 'w' || e.key === 'W') {
                    d = 0;
                } else if (e.key === 'd' || e.key === 'D') {
                    d = 1;
                } else if (e.key === 's' || e.key === 'S') {
                    d = 2;
                } else if (e.key === 'a' || e.key === 'A') {
                    d = 3;
                } else if (e.key === 'ArrowUp') {
                    d = 0;
                } else if (e.key === 'ArrowRight') {
                    d = 1;
                } else if (e.key === 'ArrowDown') {
                    d = 2;
                } else if (e.key === 'ArrowLeft') {
                    d = 3;
                }
    
                if (d >= 0 && !this.automatically_move && !this.has_input && this.time_left > 0) {
                    this.store.state.pk.socket.send(JSON.stringify({
                        event: "move",
                        direction: d
                    }));
                    this.has_input = true;
                }
            });
        }
    }

    start() {
        this.create_walls();
        this.add_listening_events();
    }

    // 判断两条蛇是否都准备下一回合了
    check_ready() {
        for (const snake of this.snakes) {
            if (snake.status !== "idle") return false;
            if (snake.direction === -1) return false;
        }
        return true;
    }

    // 判断对局是否结束
    check_end() {
        for (const snake of this.snakes) {
            if (snake.status === "die") return true;
        }
        return false;
    }

    // 让两条蛇进入下一回合
    next_step() {
        for (const snake of this.snakes) {
            snake.next_step();
        }
        this.time_left = this.schedule_time;
        this.has_input = false;
        this.steps ++ ;
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols,
                        this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update_timer() {
        if (!this.timer) return;

        if (this.check_end()) {
            this.timer.innerText = "对局结束";
        } else if (this.automatically_move) {
            this.timer.innerText = "等待对手输入";
        } else if (!this.has_input) {
            this.time_left -= this.timedelta;
            if (this.time_left < 0) {
                this.time_left = 0;
            }
            this.timer.innerText =  `倒计时： ${parseInt(this.time_left / 1000)}`;
        } else {
            this.timer.innerText = "等待对手输入";
        }
    }

    update_round() {
        this.round.innerText = `第 ${this.steps} 回合`
    }

    update() {
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
            this.update_round();
        }
        this.update_timer();
        this.render();
    }

    render() {
        const color_even = "#AAD751", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r ++ ) {
            for (let c = 0; c < this.cols; c ++ ) {
                if ((r + c) % 2 === 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L);
            }
        }
    }
}