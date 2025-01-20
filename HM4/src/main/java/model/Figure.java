package model;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public abstract class Figure {
    // Сторона a
    int a;
    // Сторона b
    int b;

    public int getPerimeter() {
        return 0;
    }
}
