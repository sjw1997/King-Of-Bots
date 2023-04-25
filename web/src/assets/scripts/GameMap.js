import { AcGameObject } from "./AcGameObject";
import { Snake } from "./Snake";
import { Wall } from "./Wall";

export class GameMap extends AcGameObject {
    constructor(ctx, parent, store) {
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

            if (d >= 0) {
                this.store.state.pk.socket.send(JSON.stringify({
                    event: "move",
                    direction: d
                }));
            }
        });
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

    // 让两条蛇进入下一回合
    next_step() {
        for (const snake of this.snakes) {
            snake.next_step();
        }
    }

    update_size() {
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols,
                        this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update() {
        this.update_size();
        if (this.check_ready()) {
            this.next_step();
        }
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