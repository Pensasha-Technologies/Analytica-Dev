package com.pensasha.school.stream;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StreamService {

	@Autowired StreamRespository streamRespository;
	
	//Adding stream to school
	public Stream addStreamSchool(Stream stream) {
		return streamRespository.save(stream);
	}
	
	//updating stream
	public Stream updatestream(Stream stream) {
		return streamRespository.save(stream);
	}
	
	//delete stream
	public void  deleteStream(int id) {
		streamRespository.deleteById(id);
	}
	
	//Getting all streams
	public List<Stream> getAllStreams() {
		return streamRespository.findAll();
	}
	
	//Getting all streams in school
	public List<Stream> getStreamsInSchool(int code){
		return streamRespository.findBySchoolCode(code);
	}
	
	//getting one stream
	public Stream getStream(int id) {
		return streamRespository.findById(id).get();
	}
	
	//Getting stream by stream
	public Stream getStreamByStream(String stream) {
		return streamRespository.findByStream(stream);
	}
	
	//Does stream exist
	public boolean doesStreamExistInSchool(int id, int code) {
		return streamRespository.existsByIdAndSchoolCode(id, code);
	}
}
