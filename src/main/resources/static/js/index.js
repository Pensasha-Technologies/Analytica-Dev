function getSubjects() {

	var $form = $('#formMarksInput');
	var $year = $('#yearMarksInput');
	var $stream = $('#streamMarksInput');
	var $subject = $('#subjectMarksInput');
	var $term = $('#termMarksInput');
	var $code = $('#code');
	var $examType = $('#examMarksType');
	

	window.location.href='/schools/'+ $code.val() + '/years/' + $year.val() +'/forms/' + $form.val() + '/terms/' + $term.val() + '/subjects/' + $subject.val() + '/streams/' + $stream.val() + '/exams/'+ $examType.val();
}

function getTeacherSubjects() {

	var $form = $('#formMarksInput');
	var $year = $('#yearMarksInput');
	var $stream = $('#streamMarksInput');
	var $subject = $('#subjectMarksInput');
	var $term = $('#termMarksInput');
	var $code = $('#inputMarksCode');
	var $examType = $('#examMarksType');
	

	window.location.href='/schools/'+ $code.val() + '/years/' + $year.val() +'/forms/' + $form.val() + '/terms/' + $term.val() + '/subjects/' + $subject.val() + '/streams/' + $stream.val() + '/exams/'+ $examType.val();
}




