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

        const [snake0, snake1] = this.snakes;
        this.ctx.canvas.addEventListener("keydown", e => {
            if (e.key === 'w' || e.key === 'W') {
                snake0.set_direction(0);
            } else if (e.key === 'd' || e.key === 'D') {
                snake0.set_direction(1);
            } else if (e.key === 's' || e.key === 'S') {
                snake0.set_direction(2);
            } else if (e.key === 'a' || e.key === 'A') {
                snake0.set_direction(3);
            } else if (e.key === 'ArrowUp') {
                snake1.set_direction(0);
            } else if (e.key === 'ArrowRight') {
                snake1.set_direction(1);
            } else if (e.key === 'ArrowDown') {
                snake1.set_direction(2);
            } else if (e.key === 'ArrowLeft') {
                snake1.set_direction(3);
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

    // 检测目标位置是否合法，没有撞到两条蛇的身体和障碍物
    check_valid(cell) {
        for (const wall of this.walls) {
            if (wall.r === cell.r && wall.c === cell.c) {
                return false;
            }
        }

        for (const snake of this.snakes) {
            let k = snake.cells.length;

            // 当蛇尾会前进的时候，蛇尾不要判断
            if (!snake.check_tail_increasing()) {
                k -- ;
            }

            for (let i = 0; i < k; i ++ ) {
                if (snake.cells[i].r === cell.r &&
                    snake.cells[i].c === cell.c)
                    return false;
            }
        }
        return true;
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