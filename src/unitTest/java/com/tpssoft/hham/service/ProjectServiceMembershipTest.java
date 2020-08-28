package com.tpssoft.hham.service;

import com.tpssoft.hham.entity.Project;
import com.tpssoft.hham.exception.ResourceNotFoundException;
import com.tpssoft.hham.repository.MembershipRepository;
import com.tpssoft.hham.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceMembershipTest {
//    ProjectRepository projectRepository = mock(ProjectRepository.class);
//    MembershipRepository membershipRepository = mock(MembershipRepository.class);
//    ServiceHelper serviceHelper = mock(ServiceHelper.class);
//    ProjectService service = new ProjectService(serviceHelper, projectRepository, membershipRepository);
//    int projectId = 1;
//    int userId = 1;
//    Optional<Project> optionalEmptyProject = Optional.of(new Project());

//    @Test
//    @DisplayName("`isAdminId()` throws when the specified project does not exist")
//    void isAdminIdThrowsWhenTheSpecifiedProjectDoesNotExist() {
//        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
//        assertThrows(ResourceNotFoundException.class, () -> service.isAdminId(projectId, userId));
//    }
//
//    @Test
//    @DisplayName("`isAdminId()` return false when the specified user is not in the project")
//    void isAdminIdReturnsFalseWhenTheSpecifiedUserIsNotInProject() {
//        when(projectRepository.findById(projectId)).thenReturn(optionalEmptyProject);
//        assertFalse(service.isAdminId(projectId, userId));
//    }
//
//    @Test
//    @DisplayName("`isAdminId()` return `false` when the specified user is not an admin of the project")
//    void isAdminIdReturnsFalseWhenTheSpecifiedUserIsNotProjectAdmin() {
//        var project = new Project();
//        project.getMemberships().add(new Membership(projectId, userId, false));
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
//        assertFalse(service.isAdminId(projectId, userId));
//    }
//
//    @Test
//    @DisplayName("`isAdminId()` return `true` when the specified user is an admin of the project")
//    void isAdminIdReturnsTrueWhenTheSpecifiedUserIsProjectAdmin() {
//        var project = new Project();
//        project.getMemberships().add(new Membership(projectId, userId, true));
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
//        assertTrue(service.isAdminId(projectId, userId));
//    }
//
//    @Test
//    @DisplayName("`isMemberId()` throws when the specified project does not exist")
//    void isMemberIdThrowsWhenTheSpecifiedProjectDoesNotExist() {
//        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
//        assertThrows(ResourceNotFoundException.class, () -> service.isMemberId(projectId, userId));
//    }
//
//    @Test
//    @DisplayName("`isMemberId()` return false when the specified user is not in the project")
//    void isMemberIdReturnsFalseWhenTheSpecifiedUserIsNotInProject() {
//        when(projectRepository.findById(projectId)).thenReturn(optionalEmptyProject);
//        assertFalse(service.isMemberId(projectId, userId));
//    }
//
//    @Test
//    @DisplayName("`isMemberId()` return `true` when the specified user is a member of the project")
//    void isMemberIdReturnsTrueWhenTheSpecifiedUserIsInProject() {
//        var project = new Project();
//        project.getMemberships().add(new Membership(projectId, userId));
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
//        assertTrue(service.isMemberId(projectId, userId));
//    }
//
//    @Test
//    @DisplayName("Project admin is also a member")
//    void adminIsAMemberOfProject() {
//        var project = new Project();
//        project.getMemberships().add(new Membership(projectId, userId, true));
//        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
//        assertTrue(service.isMemberId(projectId, userId));
//    }

//    @Test
//    @DisplayName("`getAdmins()` throws when the specified project is not found")
//    void getAdminsThrowsWhenProjectNotFound() {
//        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
//        assertThrows(ResourceNotFoundException.class, () -> service.getAdmins(projectId));
//    }
//
//    @Test
//    @DisplayName("`getMembers()` throws when the specified project is not found")
//    void getMembersThrowsWhenProjectNotFound() {
//        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());
//        assertThrows(ResourceNotFoundException.class, () -> service.getMembers(projectId));
//    }
}
