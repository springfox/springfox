package com.mangofactory.swagger.springmvc.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wordnik.swagger.core.Api;
import com.wordnik.swagger.core.ApiError;
import com.wordnik.swagger.core.ApiErrors;
import com.wordnik.swagger.core.ApiOperation;
import com.wordnik.swagger.core.ApiParam;

@Controller
@RequestMapping("/books")
@Api(value="", description="Operations about books")
public class BookService {

	@RequestMapping(value="/{bookId}",method=RequestMethod.GET)
	@ApiOperation(value = "Find book by ID", notes = "Returns a book when ID < 10. "
			+ "ID > 10 or nonintegers will simulate API error conditions", responseClass = "com.wordnik.swagger.sample.model.book")
	@ApiErrors(value = { @ApiError(code = 400, reason = "Invalid ID supplied"),
			@ApiError(code = 404, reason = "book not found") })
	public Book getBookById(
			@ApiParam(value = "ID of book that needs to be fetched", allowableValues = "range[1,5]", required = true) @PathVariable("bookId") String bookId
			)
	{
		return null;
	}
}
