package cn.cqut.backup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Word {
    private String value;
    private int id;
    private int line;
    private String category;
    private String type;
}

