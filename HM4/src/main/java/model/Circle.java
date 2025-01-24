package model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class Circle extends Ellipse implements MovableInterface{
    int x;
    int y;

    public Circle(int a, int b) {
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
        return "Circle: a=" + a + ", b=" + b + ", x=" + x + ", y=" + y;
    }
}
