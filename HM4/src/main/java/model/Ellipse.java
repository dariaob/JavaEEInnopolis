package model;

import lombok.*;

@NoArgsConstructor
@ToString
@Getter
@Setter
public class Ellipse extends Figure{
    public Ellipse(int a, int b) {
        super(a, b);
    }
    @Override
    public int getPerimeter() {
        return (int) (2 * Math.PI * Math.sqrt((Math.pow(a, 2) + Math.pow(a, 2)) / 2));
    }
}
