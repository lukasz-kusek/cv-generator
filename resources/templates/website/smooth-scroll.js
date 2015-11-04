/* // Smooth Scrolling //////////////////////////////////////
// // By Chris Coyer - css-tricks.com ///////////////////////
// // www.css-tricks.com/snippets/jquery/smooth-scrolling/ */
// 						------------					   //
// I made two calls with different settings so i could set //
// different scroll postions for sidebar's and year's Nav  //
/////////////////////////////////////////////////////////////

var jump=function(e)
{
       //prevent the "normal" behaviour which would be a "hard" jump
       e.preventDefault();
       //Get the target
       var target = $(this).attr("href");
       //perform animated scrolling
       $('html,body').animate(
       {
               //get top-position of target-element and set it as scroll target
               scrollTop: $(target).offset().top - 250
       //scrolldelay: 2 seconds
       },700);
}

var jump2=function(e)
{
       //prevent the "normal" behaviour which would be a "hard" jump
       e.preventDefault();
       //Get the target
       var target2 = $(this).attr("href");
       //perform animated scrolling
       $('html,body').animate(
       {
               //get top-position of target-element and set it as scroll target
               scrollTop: $(target2).offset().top - 312
       //scrolldelay: 2 seconds
       },700);
}