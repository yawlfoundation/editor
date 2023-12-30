-----------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_twitter" => {
                one s: State |  t in s.token &&

s.login_by_twitter = 1 && s.login_by_google = 0 && s.login_by_linkedin = 1 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_twitter" => {
                one s: State |  t in s.token &&

s.login_by_twitter = 1 && s.login_by_google = 0 && s.login_by_linkedin = 0 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_twitter" => {
                one s: State |  t in s.token &&

s.login_by_linkedin = 0 && s.login_by_google = 0 && s.login_by_twitter = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_twitter" => {
                one s: State |  t in s.token &&

s.login_by_linkedin = 0 && s.login_by_google = 0 && s.login_by_twitter = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_twitter" => {
                one s: State |  t in s.token &&

s.login_by_linkedin = 0 && s.login_by_twitter = 1 && s.login_by_google = 1 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_twitter" => {
                one s: State |  t in s.token &&

s.login_by_linkedin = 0 && s.login_by_twitter = 1 && s.login_by_google = 0 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
	all t, t': task | t.label = "start_login" && t'.label = "login_by_google" => {
    one s: State |  t in s.token && t' in s.next.token && 1 = 1}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_linkedin" => {
                one s: State |  t in s.token &&

s.login_by_twitter = 0 && s.login_by_google = 0 && s.login_by_linkedin = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_linkedin" => {
                one s: State |  t in s.token &&

s.login_by_twitter = 0 && s.login_by_google = 0 && s.login_by_linkedin = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_linkedin" => {
                one s: State |  t in s.token &&

s.login_by_linkedin = 1 && s.login_by_google = 0 && s.login_by_twitter = 1 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_linkedin" => {
                one s: State |  t in s.token &&

s.login_by_linkedin = 1 && s.login_by_google = 0 && s.login_by_twitter = 0 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_linkedin" => {
                one s: State |  t in s.token &&

s.login_by_linkedin = 1 && s.login_by_twitter = 0 && s.login_by_google = 1 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_login" && t'.label = "login_by_linkedin" => {
                one s: State |  t in s.token &&

s.login_by_linkedin = 1 && s.login_by_twitter = 0 && s.login_by_google = 0 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "payment_confirmer" && t'.label = "finsh_shopping" => {
                one s: State |  t in s.token &&

s.pay_by_paypal = 1 && s.pay_by_credit_card_successful = 2 && s.pay_by_credit_card = 1 && s.pay_by_paypal_successful = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "payment_confirmer" && t'.label = "finsh_shopping" => {
                one s: State |  t in s.token &&

s.pay_by_paypal = 1 && s.pay_by_credit_card_successful = 2 && s.pay_by_credit_card = 1 && s.pay_by_paypal_successful = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "payment_confirmer" && t'.label = "finsh_shopping" => {
                one s: State |  t in s.token &&

s.pay_by_paypal_successful = 1 && s.pay_by_credit_card_successful = 2 && s.pay_by_credit_card = 1 && s.pay_by_paypal = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "payment_confirmer" && t'.label = "finsh_shopping" => {
                one s: State |  t in s.token &&

s.pay_by_paypal_successful = 1 && s.pay_by_credit_card_successful = 2 && s.pay_by_credit_card = 1 && s.pay_by_paypal = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "payment_confirmer" && t'.label = "finsh_shopping" => {
                one s: State |  t in s.token &&

s.pay_by_paypal_successful = 2 && s.pay_by_paypal = 1 && s.pay_by_credit_card = 1 && s.pay_by_credit_card_successful = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "payment_confirmer" && t'.label = "finsh_shopping" => {
                one s: State |  t in s.token &&

s.pay_by_paypal_successful = 2 && s.pay_by_paypal = 1 && s.pay_by_credit_card = 1 && s.pay_by_credit_card_successful = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "payment_confirmer" && t'.label = "finsh_shopping" => {
                one s: State |  t in s.token &&

s.pay_by_paypal_successful = 2 && s.pay_by_paypal = 1 && s.pay_by_credit_card_successful = 1 && s.pay_by_credit_card = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "payment_confirmer" && t'.label = "finsh_shopping" => {
                one s: State |  t in s.token &&

s.pay_by_paypal_successful = 2 && s.pay_by_paypal = 1 && s.pay_by_credit_card_successful = 1 && s.pay_by_credit_card = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
	all t, t': task | t.label = "payment_confirmer" && t'.label = "start_payment" => {
    one s: State |  t in s.token && t' in s.next.token && 1 = 1}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "logging_in" && t'.label = "item_picker" => {
                one s: State |  t in s.token &&

s.login_successful = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "logging_in" && t'.label = "item_picker" => {
                one s: State |  t in s.token &&

s.login_successful = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
	all t, t': task | t.label = "logging_in" && t'.label = "start_shopping" => {
    one s: State |  t in s.token && t' in s.next.token && 1 = 1}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
	all t, t': task | t.label = "start_payment" && t'.label = "pay_by_credit_card" => {
    one s: State |  t in s.token && t' in s.next.token && 1 = 1}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_payment" && t'.label = "pay_by_paypal" => {
                one s: State |  t in s.token &&

s.pay_by_credit_card = 0 && s.pay_by_paypal = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_payment" && t'.label = "pay_by_paypal" => {
                one s: State |  t in s.token &&

s.pay_by_credit_card = 0 && s.pay_by_paypal = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_payment" && t'.label = "pay_by_paypal" => {
                one s: State |  t in s.token &&

s.pay_by_paypal = 1 && s.pay_by_credit_card = 1 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_payment" && t'.label = "pay_by_paypal" => {
                one s: State |  t in s.token &&

s.pay_by_paypal = 1 && s.pay_by_credit_card = 0 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "security_checker" && t'.label = "start_login" => {
                one s: State |  t in s.token &&

s.security_check_pass = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "security_checker" && t'.label = "start_login" => {
                one s: State |  t in s.token &&

s.security_check_pass = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
	all t, t': task | t.label = "security_checker" && t'.label = "finsh_shopping" => {
    one s: State |  t in s.token && t' in s.next.token && 1 = 1}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
	all t, t': task | t.label = "start_shopping" && t'.label = "start_login" => {
    one s: State |  t in s.token && t' in s.next.token && 1 = 1}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_shopping" && t'.label = "security_checker" => {
                one s: State |  t in s.token &&

s.need_security_checking = 2 && t' not in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

----------------------------------------------------------------------------------------------
    /* Impose an ordering on the State. */
open util/ordering[State]
sig State {
 	token, n_token: some Object1,
	login_by_twitter: lone Int, 
	pay_by_credit_card: lone Int, 
	login_by_google: lone Int, 
	pay_by_credit_card_successful: lone Int, 
	need_security_checking: lone Int, 
	security_check_pass: lone Int, 
	pay_by_paypal_successful: lone Int, 
	login_by_linkedin: lone Int, 
	login_successful: lone Int, 
	pay_by_paypal: lone Int, 
}

 // formal defintion of model objects
 abstract sig Object1
 {
 flowsInto: set Flows,
 // status: String
 }



  one sig input_condition 	extends Object1 {}
 one sig output_condition 	extends Object1 {}


 sig task extends Object1 
 {
 split, join, label: String,
  last_deactive_task: Object1, 
 cancellation_reigon_objects: Object1}

 /*
 fact{
 all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
 }
 */

 sig Flows 
 {
 predicate: lone Boolean,
  nextTask: one Object1}//formal definition of boolean 
 sig Boolean 
 {
 value: Int
 }
 fact{
 all b: Boolean | b.value = 0 || b.value = 1
 }


 //formal definition of states of the model
 //formal definition of the initial state
 fact
 {
 all s: State
 {
 s.token + s.n_token = Object1 && s.token & s.n_token = none
 }
 }
 fact
 {
 first.token = input_condition && first.n_token = Object1 - input_condition
 // && first.token.status = "Activated"
 }


   //formal definition of the final state
  fact
  {
  last.token = output_condition
  }
  fact
  {
  no s: State | output_condition in s.token && s != last
  }
  
  
  
             // task with None split have no predicate
  fact
  {
  all t: task
  {
  t.split = "None" => #t.flowsInto.predicate = 0
  }
  }
  
  
  

  // Definition of task statuses
  
 /* 
  
  
  
  fact {
  	one o: input_condition | o.status = "Activated"
  }
  
  
  
             fact{
  	all s: State {
  		all x: s.token {
  			(x.join = "None" && x != input_condition && (flowsInto.nextTask.x.split = "None" || flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status
  		}
  	}
  }
  

  fact {
  	all t: task {
  			 flowsInto.nextTask.t.status = "Deactive" && t.join = "None" => t.status = "Deactive"
  		}
  }
  
  
  
  
  
            fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Xor" => {one f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "And" => {all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" }
  		}
  	}
  }
  
  
  
             fact{
  	all s: State, s': s.next {
  		all x: s.token {
  			x.split = "Or" => {some f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = "Activated" && all f': (x.flowsInto  -  f)| f'.nextTask.status = "Deactive" }
  		}
  	}
  }
  
  */
  
  
  
            // Definition of Cancellation Reigons
  

  fact {
  	all s: State, s':s.next {
  		all x: s.token {
  			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&
  											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
  		}
  	}
  }
  
  
  
  
  
  
  
           // Definition of the None split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "None" => 	(
  (x.flowsInto.nextTask in s'.token || x in s'.token) &&
  !(x.flowsInto.nextTask in s'.token && x in s'.token)
  				)
  }
  }
  }
  // Definition of the And split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "And" => 		(
  (s'.token = x || all y: x.flowsInto.nextTask | y in s'.token) &&
  !(s'.token = x && all y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  

  // Definition of the Xor split gateway behavior
  

  fact {
  	all s: State, s': s.next {
  		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
  					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>
  					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
  		}
  	}
  }
  

  //fact {
  //	all s: State, s': s.next {
  	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
  	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>
  	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
  	//	}
  	//}
  //}
  

  // Definition of the Or split gateway behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s.token
  {
  x.split = "Or" => 		(
  (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&
  !(x in s'.token && some y: x.flowsInto.nextTask | y in s'.token)
  					)
  }
  }
  }
  
  
  
             fact all_splits_flows_intos_has_predicate {
  	all t:task | (t.split = "Xor" || t.split = "And" || t.split = "Or") => all f:t.flowsInto | #f.predicate = 1
  }
  

  fact xor_split_definition{
  						    	all s: State { all x: s.token | x.split = "Xor" => one f: x.flowsInto | f.predicate.value = 1
      }
  }
  //Definition of the None join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
  x.join = "None" => 		(
  (flowsInto.nextTask.x in s.token || x in s.token) &&
  !(flowsInto.nextTask.x in s.token && x in s.token)
  					)
  }
  }
  }
  // Definition of the And join behavior
  fact
  {
  all s: State, s': s.next
  {
  all x: s'.token
  {
 //	x.status = "Activated" &&
  x.join = "And" =>  (
  (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  !(all y: flowsInto.nextTask.x | y in s.token && x in s.token)
  				)
  }
  }
  }
  // Definition of the Xor join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Xor" => ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

  // Definition of the Or join gateway behavior
  fact{
  	all s: State, s': s.next{
  		all x: s'.token{
  			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&
  						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
  		}
  	}
  }
  

 /* fact{
  	all s: State, s': s.next{
  		all x: s'.token{
 // x.status = "Activated" &&
  			x.join = "Or" => all y: flowsInto.nextTask.x | y.status != "N/A"
  		}
  	}
  }
  */

  //Output condition has no output
  fact
   {
  #output_condition.flowsInto = 0
  }
  // Input condition next state tokens are all input condition next tasks
  fact
  {
  first.next.token = input_condition.flowsInto.nextTask
  }
  // Input condition Flows has no predicate
  fact
  {
  #first.token.flowsInto.predicate = 0
  }
  // Two distinct Object1 has no same Flow
  fact
  {
  all o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2
  }
  // To prevent state token jump forward or backward
  fact
  {
  all s: State, s': s.next | all t': Object1 | t' in s'.token =>
   some t: Object1 | t in s.token && (t' = t || t in flowsInto.nextTask.t')
  }
  


fact{ all s: State | all i: input_condition |  i in s.token =>
{ one t: Object1 | t in i.flowsInto.nextTask && t.label = "start_shopping" && t.join = "Xor" && t.split = "Xor" && t in s.next.token}

}

fact
{all t: Object1 | (t.label = "start_shopping" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_login" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_login" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "login_by_linkedin"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "login_by_google"
 && t1.split = "None" && t1.join = "None" } && 
{ one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "login_by_twitter"
 && t2.split = "None" && t2.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_google" => { one f: t.flowsInto | f.nextTask.label = "login_by_google" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_twitter" => { one f: t.flowsInto | f.nextTask.label = "login_by_twitter" && f.predicate.value = 1 && (((s.login_by_linkedin = 0) && (s.login_by_twitter = 1)) && (s.login_by_google = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_login" && t' in s'.token &&
t'.label = "login_by_linkedin" => { one f: t.flowsInto | f.nextTask.label = "login_by_linkedin" && f.predicate.value = 1 && (((s.login_by_linkedin = 1) && (s.login_by_twitter = 0)) && (s.login_by_google = 0))}
}
fact
{all t: Object1 | (t.label = "pay_by_credit_card" || t.label = "pay_by_paypal") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "payment_confirmer" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "payment_confirmer" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_payment"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "start_payment" => { one f: t.flowsInto | f.nextTask.label = "start_payment" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "payment_confirmer" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 && (((s.s.pay_by_paypal_successful = 1) && (s.pay_by_paypal = 1)) || ((s.s.pay_by_credit_card_successful = 1) && (s.pay_by_credit_card = 1)))}
}

fact {
all t: task | t.label = "item_picker" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "start_payment" && t1.split = "Xor" && t1.join = "Xor"}}fact
{all t: Object1 | (t.label = "payment_confirmer" || t.label = "security_checker") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "finsh_shopping" && t2.join = "Xor" && t2.split = "None"
    }
}


fact {
all t: task | t.label = "finsh_shopping" => {
one t1: Object1 | t1 = t.flowsInto.nextTask && t1= output_condition}}fact
{all t: Object1 | (t.label = "login_by_google" || t.label = "login_by_linkedin" || t.label = "login_by_twitter") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "logging_in" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "logging_in" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "start_shopping"
 && t0.split = "Xor" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "item_picker"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "start_shopping" => { one f: t.flowsInto | f.nextTask.label = "start_shopping" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "logging_in" && t' in s'.token &&
t'.label = "item_picker" => { one f: t.flowsInto | f.nextTask.label = "item_picker" && f.predicate.value = 1 && s.login_successful = 1}
}
fact
{all t: Object1 | (t.label = "item_picker" || t.label = "payment_confirmer") =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_payment" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_payment" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "pay_by_credit_card"
 && t0.split = "None" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "pay_by_paypal"
 && t1.split = "None" && t1.join = "None" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_paypal" => { one f: t.flowsInto | f.nextTask.label = "pay_by_paypal" && f.predicate.value = 1 && ((s.pay_by_paypal = 1) && (s.pay_by_credit_card = 0))}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_payment" && t' in s'.token &&
t'.label = "pay_by_credit_card" => { one f: t.flowsInto | f.nextTask.label = "pay_by_credit_card" && f.predicate.value = 1 }
}

fact {
all t: task | t.label = "pay_by_credit_card" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_twitter" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact{
    all t: task | t.label = "security_checker" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "finsh_shopping"
 && t0.split = "None" && t0.join = "Xor" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 && s.security_check_pass = 1}
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "security_checker" && t' in s'.token &&
t'.label = "finsh_shopping" => { one f: t.flowsInto | f.nextTask.label = "finsh_shopping" && f.predicate.value = 1 }
}
fact
{all t: Object1 | (t.label = "logging_in" || t = input_condition) =>
    {
        one t2: Object1 | t2 in t.flowsInto.nextTask && t2.label = "start_shopping" && t2.join = "Xor" && t2.split = "Xor"
    }
}


fact{
    all t: task | t.label = "start_shopping" => {
    { one t0: Object1 | t0 in t.flowsInto.nextTask && t0.label = "security_checker"
 && t0.split = "Xor" && t0.join = "None" } && 
{ one t1: Object1 | t1 in t.flowsInto.nextTask && t1.label = "start_login"
 && t1.split = "Xor" && t1.join = "Xor" }
    }
}

fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "start_login" => { one f: t.flowsInto | f.nextTask.label = "start_login" && f.predicate.value = 1 }
}


fact{
	all s: State, s': s.next | all t: task, t':Object1 | t in s.token && t.label = "start_shopping" && t' in s'.token &&
t'.label = "security_checker" => { one f: t.flowsInto | f.nextTask.label = "security_checker" && f.predicate.value = 1 && s.need_security_checking = 1}
}

fact {
all t: task | t.label = "login_by_linkedin" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "login_by_google" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "logging_in" && t1.split = "Xor" && t1.join = "Xor"}}
fact {
all t: task | t.label = "pay_by_paypal" => {
one t1: task | t1 = t.flowsInto.nextTask && t1.label = "payment_confirmer" && t1.split = "Xor" && t1.join = "Xor"}}

fact test {
            	all t, t': task | t.label = "start_shopping" && t'.label = "security_checker" => {
                one s: State |  t in s.token &&

s.need_security_checking = 1 && t' in s.next.token
 	}
}

 pred show{}
 run show for 13 but 13 task, 21 Flows 

-----------------------------------------------