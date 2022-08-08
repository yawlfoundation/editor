package org.yawlfoundation.yawl.analyser.util.alloy;

public class Constants {
    public static final String staticAlloyDefinitions = """
            /* Impose an ordering on the State. */
            open util/ordering[State]
            // formal defintion of model objects
            abstract sig Object1\s
            {
            flowsInto: set Flows,
            status: String
            }
                        
                        
            one sig input_condition 	extends Object1 {}
                        
            one sig output_condition 	extends Object1 {}
                        
                        
            sig task extends Object1
             {
            split, join, label: String,
            last_deactive_task: Object1,
            cancellation_reigon_objects: Object1,
            }
                        
                        
            fact{
            	all o: Object1 | o.status = "Activated" || o.status = "Deactive" || o.status = "N/A"
            }
                        
                        
            sig Flows 				
            {
            predicate: lone Boolean,
            nextTask: one Object1
            }
                        
                        
            //formal definition of boolean\s
            sig Boolean
            {
            value: Int
            }
            fact
            {
            all b: Boolean | b.value = 0 || b.value = 1
            }
                        
                        
            //formal definition of states of the model
            sig State 				
            {
            token, n_token: some Object1,
            a_variable: one Int,
            b_variable: one Int,
            }
                        
            fact {
            all s: State | s.a_variable = 4
            }
                        
            fact {
            all s: State | s.b_variable = 3
            }
            //formal definition of the initial state
            fact\s
            {
            all s: State\s
            {
            s.token + s.n_token = Object1 && s.token & s.n_token = none\s
            }
            }
            fact 					
            {
            first.token = input_condition && first.n_token = Object1 - input_condition && first.token.status = "Activated"
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
                        
                        
            // task with And split have no predicate
            fact
            {
            all t: task
            {
            t.split = "And" => #t.flowsInto.predicate = 0
            }
            }
                        
            // Definition of task statuses
                        
                        
                        
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
                        
                        
                        
            // Definition of Cancellation Reigons
                        
            fact {
            	all s: State, s':s.next {
            		all x: s.token {
            			all t:x.flowsInto.nextTask | t in s'.token => {no o: x.cancellation_reigon_objects | o in s'.token } &&\s
            											{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}
            		}
            	}
            }
                        
                        
                        
                        
            // Definition of the None split gateway behavior
            fact 						
            {
            all s: State, s': s.next\s
            {
            all x: s.token\s
            {
            x.split = "None" => 	(
            (x.flowsInto.nextTask in s'.token || x in s'.token) &&
            !(x.flowsInto.nextTask in s'.token && x in s'.token)
            				)\s
            }
            }
            }
            // Definition of the And split gateway behavior
            fact
            {
            all s: State, s': s.next\s
            {
            all x: s.token\s
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
            		all x: s.token{x.split = "Xor" =>((s'.token = x || all f: x.flowsInto | f.predicate.value = 1 =>\s
            					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}) &&
            					!(s'.token = x && all f: x.flowsInto | f.predicate.value = 1 =>\s
            					{one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t}))
            		}
            	}
            }
                        
            //fact {
            //	all s: State, s': s.next {
            	//	all x: s.token{x.split = "Xor" =>(s'.token = x || (one f: x.flowsInto |  f.predicate.value = 1 =>\s
            	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t})) &&
            	//				!(s'.token = x && (one f: x.flowsInto |  f.predicate.value = 1 =>\s
            	//				{ one t: f.nextTask | t in s'.token && no z: f.nextTask | z in s'.token && z != t }))
            	//	}
            	//}
            //}
                        
            // Definition of the Or split gateway behavior
            fact
            {
            all s: State, s': s.next\s
            {
            all x: s.token\s
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
            }\s
            //Definition of the None join behavior
            fact
            {
            all s: State, s': s.next\s
            {
            all x: s'.token\s
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
            all s: State, s': s.next\s
            {
            all x: s'.token\s
            {
            x.join = "And" => 	x.status = "Activated" && (
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
            			x.join = "Xor" => x.status = "Activated" && ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&\s
            						 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))
            		}
            	}
            }
                        
            // Definition of the Or join gateway behavior
            fact{
            	all s: State, s': s.next{
            		all x: s'.token{
            			x.join = "Or" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token) &&\s
            						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))
            		}
            	}
            }
                        
            fact{
            	all s: State, s': s.next{
            		all x: s'.token{
            			x.join = "Or" => x.status = "Activated" && all y: flowsInto.nextTask.x | y.status != "N/A"
            		}
            	}
            }
                        
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
                        
            """;
}
