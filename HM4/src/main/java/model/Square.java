package model;

import lombok.*;
@NoArgsConstructor
@Getter
@Setter
public class Square extends Rectangle implements MovableInterface{
    int x;
    int y;

    public Square(int a, int b) {
        super(a, b);
    }
    @Override
    public void move(int x, int y) {
        this.x += x;
        this.y += y;
    }

    // Переопределяем toString, чтобы добавить a, b, x, y
    @Override
    public String toString() {
        return "Square: a=" + a + ", b=" + b + ", x=" + x + ", y=" + y;
    }
}
