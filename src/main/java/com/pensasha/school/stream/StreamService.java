package com.pensasha.school.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StreamService {
    @Autowired
    StreamRespository streamRespository;

    public Stream addStreamSchool(Stream stream) {
        return this.streamRespository.save(stream);
    }

    public Stream updatestream(Stream stream) {
        return this.streamRespository.save(stream);
    }

    public void deleteStream(int id) {
        this.streamRespository.deleteById(id);
    }

    public List<Stream> getAllStreams() {
        return this.streamRespository.findAll();
    }

    public List<Stream> getStreamsInSchool(int code) {
        return this.streamRespository.findBySchoolCode(code);
    }

    public Stream getStream(int id) {
        return this.streamRespository.findById(id).get();
    }

    public Stream getStreamByStream(String stream) {
        return this.streamRespository.findByStream(stream);
    }

    public boolean doesStreamExistInSchool(int id, int code) {
        return this.streamRespository.existsByIdAndSchoolCode(id, code);
    }
    
    public List<Stream> getAllStreamOfTeacher(String username){
    	return this.streamRespository.findByTeachersUsername(username);
    }
}