package cn.cqut.backup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Error {
    private String word;
    private int errorId;
    private int line;
    private String errorInfo;
}
