package lk.ijse.bo.custom;

import lk.ijse.bo.SuperBO;
import lk.ijse.dto.ProgramDTO;

import java.util.List;

public interface ProgramBO extends SuperBO {
    boolean saveProgram(ProgramDTO programDTO) throws Exception;

    boolean updateProgram(ProgramDTO programDTO) throws Exception;

    boolean deleteProgram(String id) throws Exception;

    ProgramDTO searchProgram(String id) throws Exception;

    List<ProgramDTO> getAllPrograms() throws Exception;

    String getNextProgramId() throws Exception;

    boolean existsByProgramId(String id) throws Exception;
}