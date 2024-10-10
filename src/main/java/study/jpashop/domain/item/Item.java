package study.jpashop.domain.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import study.jpashop.domain.Category;
import study.jpashop.exception.NotEnoughStockException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="dtype")
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name="item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<>();

    //==비지니스 로직==//
    /**
     * stock 증가
     */
    public void addStockQuantity(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStrock(int quantity) {
        int restStock = this.stockQuantity -= quantity;
        if (restStock < 0) {
            throw  new NotEnoughStockException("need more stock");
        }

        this.stockQuantity = restStock;
    }

}
