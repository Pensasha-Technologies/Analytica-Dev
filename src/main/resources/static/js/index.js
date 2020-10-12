function getSubjects() {

	var $form = $('#formInput');
	var $year = $('#yearInput');
	var $stream = $('#streamInput');
	var $subject = $('#subjectInput');
	var $term = $('#termInput');
	var $code = $('#code');
	var $examType = $('#examType');
	

	window.location.href='/schools/'+ $code.val() + '/years/' + $year.val() +'/forms/' + $form.val() + '/terms/' + $term.val() + '/subjects/' + $subject.val() + '/streams/' + $stream.val() + '/exams/'+ $examType.val();
}


