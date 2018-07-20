package com.herokuapp.erpmesbackend.erpmesbackend.staff.teams;

import com.herokuapp.erpmesbackend.erpmesbackend.staff.employees.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequest {

    @NotNull
    private Role role;

    @NotNull
    private Long managerId;

    @NotNull
    private List<Long> employeeIds;
}
