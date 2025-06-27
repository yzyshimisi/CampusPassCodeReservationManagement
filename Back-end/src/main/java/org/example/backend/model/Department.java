package org.example.backend.model;

public class Department {
    private int id;
    private String departmentType;
    private String departmentName;

    // 无参构造函数
    public Department() {}

    // 有参构造函数
    public Department(int id, String departmentType, String departmentName) {
        this.id = id;
        this.departmentType = departmentType;
        this.departmentName = departmentName;
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(String departmentType) {
        this.departmentType = departmentType;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", departmentType='" + departmentType + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
