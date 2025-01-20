package model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Rectangle extends Figure{
    public Rectangle(int a, int b) {
        super(a, b);
    }
    @Override
    public int getPerimeter() {
        return  2 *(super.a + super.b);
    }
}
