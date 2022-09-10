package org.yawlfoundation.yawl.analyser.util.alloy;

public class Constants {
    public static final String staticAlloyDefinitions = "/* Impose an ordering on the State. */" +
            "\nopen util/ordering[State]\n// formal defintion of model objects\nabstract sig" +
            " Object1\n{\nflowsInto: set Flows,\n status: String\n}\n\n\n\n one sig input_condition 	" +
            "extends Object1 {}\none sig output_condition 	extends Object1 {}\n\n\nsig task extends Object1 " +
            "\n{\nsplit, join, label: String,\n last_deactive_task: Object1, \ncancellation_reigon_objects: " +
            "Object1}\n\n\nfact{\nall o: Object1 | o.status = \"Activated\" || o.status = \"Deactive\" " +
            "|| o.status = \"N/A\"\n}\n\n\nsig Flows \n{\npredicate: lone Boolean,\n nextTask: one Object1}" +
            "//formal definition of boolean \nsig Boolean \n{\nvalue: Int\n}\nfact{\nall b: Boolean | " +
            "b.value = 0 || b.value = 1\n}\n\n\n//formal definition of states of the model\n" +
            "//formal definition of the initial state\nfact\n{\nall s: State\n{\ns.token + s.n_token = Object1" +
            " && s.token & s.n_token = none\n}\n}\nfact\n{\nfirst.token = input_condition && first.n_token = " +
            "Object1 - input_condition && first.token.status = \"Activated\"}\n\n\n  " +
            "//formal definition of the final state\n fact\n {\n last.token = output_condition\n }\n " +
            "fact\n {\n no s: State | output_condition in s.token && s != last\n }\n \n \n \n           " +
            " // task with None split have no predicate\n fact\n {\n all t: task\n {\n t.split = \"None\" => " +
            "#t.flowsInto.predicate = 0\n }\n }\n \n \n \n // task with And split have no predicate\n " +
            "fact\n {\n all t: task\n {\n t.split = \"And\" => #t.flowsInto.predicate = 0\n }\n }" +
            "\n \n\n // Definition of task statuses\n \n \n \n \n \n fact {\n 	one o: input_condition " +
            "| o.status = \"Activated\"\n }\n \n \n \n            fact{\n 	all s: State {\n 		all x: s.token " +
            "{\n 			(x.join = \"None\" && x != input_condition && (flowsInto.nextTask.x.split = \"None\" ||" +
            " flowsInto.nextTask.x = input_condition)) => x.status = flowsInto.nextTask.x.status\n 		}\n 	}\n }" +
            "\n \n\n fact {\n 	all t: task {\n 			 flowsInto.nextTask.t.status = \"Deactive\" && t.join" +
            " = \"None\" => t.status = \"Deactive\"\n 		}\n }\n \n \n \n \n \n           fact{\n 	" +
            "all s: State, s': s.next {\n 		all x: s.token {\n 			x.split = \"Xor\" => {one f: x.flowsInto " +
            "| f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = \"Activated\" && all f': " +
            "(x.flowsInto  -  f)| f'.nextTask.status = \"Deactive\" }\n 		}\n 	}\n }\n \n \n \n            " +
            "fact{\n 	all s: State, s': s.next {\n 		all x: s.token {\n 			x.split = \"And\" => " +
            "{all f: x.flowsInto | f.predicate.value = 1 && f.nextTask in s'.token && f.nextTask.status = \"Activated\"" +
            " }\n 		}\n 	}\n }\n \n \n \n            fact{\n 	all s: State, s': s.next {\n 		" +
            "all x: s.token {\n 			x.split = \"Or\" => {some f: x.flowsInto | f.predicate.value = 1 && " +
            "f.nextTask in s'.token && f.nextTask.status = \"Activated\" && all f': (x.flowsInto  -  f)| " +
            "f'.nextTask.status = \"Deactive\" }\n 		}\n 	}\n }\n \n \n \n \n \n           " +
            "// Definition of Cancellation Reigons\n \n\n fact {\n 	all s: State, s':s.next {\n 		" +
            "all x: s.token {\n 			all t:x.flowsInto.nextTask | t in s'.token => " +
            "{no o: x.cancellation_reigon_objects | o in s'.token } &&\n 											" +
            "{all o: x.cancellation_reigon_objects { all f: o.flowsInto | f.nextTask not in s'.token }}\n 		}\n" +
            " 	}\n }\n \n \n \n \n \n \n \n          // Definition of the None split gateway behavior\n fact 		" +
            "				\n {\n all s: State, s': s.next\n {\n all x: s.token\n {\n x.split = \"None\" => 	" +
            "(\n (x.flowsInto.nextTask in s'.token || x in s'.token) &&\n !(x.flowsInto.nextTask in s'.token && x in " +
            "s'.token)\n 				)\n }\n }\n }\n // Definition of the And split gateway behavior\n fact\n" +
            " {\n all s: State, s': s.next\n {\n all x: s.token\n {\n x.split = \"And\" => 		(\n (s'.token = x" +
            " || all y: x.flowsInto.nextTask | y in s'.token) &&\n !(s'.token = x && all y: x.flowsInto.nextTask | y" +
            " in s'.token)\n 					)\n }\n }\n }\n \n\n // Definition of the Xor split gateway behavior" +
            "\n \n\n fact {\n 	all s: State, s': s.next {\n 		all x: s.token{x.split = \"Xor\" =>((s'.token = x" +
            " || all f: x.flowsInto | f.predicate.value = 1 =>\n 					{one t: f.nextTask | t in" +
            " s'.token && no z: f.nextTask | z in s'.token && z != t}) &&\n 					!(s'.token = x " +
            "&& all f: x.flowsInto | f.predicate.value = 1 =>\n 					{one t: f.nextTask | t in" +
            " s'.token && no z: f.nextTask | z in s'.token && z != t}))\n 		}\n 	}\n }\n \n\n //fact {\n //	" +
            "all s: State, s': s.next {\n 	//	all x: s.token{x.split = \"Xor\" =>(s'.token = x || " +
            "(one f: x.flowsInto |  f.predicate.value = 1 =>\n 	//				{ one t: f.nextTask | t in " +
            "s'.token && no z: f.nextTask | z in s'.token && z != t})) &&\n 	//				!(s'.token = x && " +
            "(one f: x.flowsInto |  f.predicate.value = 1 =>\n 	//				{ one t: f.nextTask | t in s'.token " +
            "&& no z: f.nextTask | z in s'.token && z != t }))\n 	//	}\n 	//}\n //}\n \n\n // Definition of the" +
            " Or split gateway behavior\n fact\n {\n all s: State, s': s.next\n {\n all x: s.token\n {\n x.split =" +
            " \"Or\" => 		(\n (x in s'.token || some y: x.flowsInto.nextTask | y in s'.token) &&\n !(x in" +
            " s'.token && some y: x.flowsInto.nextTask | y in s'.token)\n 					)\n }\n }\n }\n \n \n \n  " +
            "          fact all_splits_flows_intos_has_predicate {\n 	all t:task | (t.split = \"Xor\" || t.split = " +
            "\"And\" || t.split = \"Or\") => all f:t.flowsInto | #f.predicate = 1\n }\n \n\n fact " +
            "xor_split_definition{\n 						    	all s: State { all x: s.token | x.split = " +
            "\"Xor\" => one f: x.flowsInto | f.predicate.value = 1\n     }\n }\n //Definition of the None join " +
            "behavior\n fact\n {\n all s: State, s': s.next\n {\n all x: s'.token\n {\n x.join = \"None\" => 		" +
            "(\n (flowsInto.nextTask.x in s.token || x in s.token) &&\n !(flowsInto.nextTask.x in s.token && x in" +
            " s.token)\n 					)\n }\n }\n }\n // Definition of the And join behavior\n fact\n {\n all" +
            " s: State, s': s.next\n {\n all x: s'.token\n {\n x.join = \"And\" => 	x.status = \"Activated\" && " +
            "(\n (all y: flowsInto.nextTask.x | y in s.token || x in s.token) &&\n !(all y: flowsInto.nextTask.x " +
            "| y in s.token && x in s.token)\n 				)\n }\n }\n }\n // Definition of the Xor join gateway " +
            "behavior\n fact{\n 	all s: State, s': s.next{\n 		all x: s'.token{\n 			x.join = \"Xor\" => " +
            "x.status = \"Activated\" && ((one y: flowsInto.nextTask.x | y in s.token || x in s.token) &&\n 			" +
            "			 !(one y: flowsInto.nextTask.x | y in s.token && x in s.token))\n 		}\n 	}\n }\n \n\n" +
            " // Definition of the Or join gateway behavior\n fact{\n 	all s: State, s': s.next{\n 		all x: " +
            "s'.token{\n 			x.join = \"Or\" =>((some y: flowsInto.nextTask.x | y in s.token || x in s.token)" +
            " &&\n 						 !(some y: flowsInto.nextTask.x | y in s.token && x in s.token))\n 		}\n " +
            "	}\n }\n \n\n fact{\n 	all s: State, s': s.next{\n 		all x: s'.token{\n 			x.join = \"Or\"" +
            " => x.status = \"Activated\" && all y: flowsInto.nextTask.x | y.status != \"N/A\"\n 		}\n 	" +
            "}\n }\n \n\n //Output condition has no output\n fact\n  {\n #output_condition.flowsInto = 0\n }\n " +
            "// Input condition next state tokens are all input condition next tasks\n fact\n {\n first.next.token = " +
            "input_condition.flowsInto.nextTask\n }\n // Input condition Flows has no predicate\n fact\n {\n " +
            "#first.token.flowsInto.predicate = 0\n }\n // Two distinct Object1 has no same Flow\n fact\n {\n all" +
            " o1, o2: Object1 | all f1: o1.flowsInto, f2: o2.flowsInto | o1 != o2 => f1 != f2\n }\n " +
            "// To prevent state token jump forward or backward\n fact\n {\n all s: State, s': s.next | all " +
            "t': Object1 | t' in s'.token =>\n  some t: Object1 | t in s.token && (t' = t || t in " +
            "flowsInto.nextTask.t')\n }\n \n\n ";
}